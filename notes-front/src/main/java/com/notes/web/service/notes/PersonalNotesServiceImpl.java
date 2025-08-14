package com.notes.web.service.notes;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.notes.domain.front.notes.PersonalNotes;
import com.notes.mapper.front.notes.ESNotesRepository;
import com.notes.mapper.front.notes.PersonalNotesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;

/**
 * <p>
 * 个人笔记表 服务实现类
 * </p>
 *
 * @author ldj
 * @since 2025-04-30
 */
@Service
public class PersonalNotesServiceImpl extends ServiceImpl<PersonalNotesMapper, PersonalNotes> implements IPersonalNotesService {
    /*@Autowired
    private ESNotesRepository esNotesRepository;

    @Autowired
    private EmbeddingsService embeddingsService;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;*/

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private IRagNotesService ragNotesService;


    /*@Override
    public void updateEs() {
        Long userId = FrontSecurityUtils.getUserId();
        LambdaQueryWrapper<PersonalNotes> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PersonalNotes::getNotesUserId, userId);
        wrapper.eq(PersonalNotes::getRecycle, false);
        wrapper.ne(PersonalNotes::getStoreEsTime, TimeConstants.DEFAULT_TIME);
        wrapper.le(PersonalNotes::getStoreEsTime, LocalDateTime.now().minusDays(7));
        wrapper.apply("CHAR_LENGTH(content) > 10");
        List<PersonalNotes> list = this.list(wrapper);
        List<ESNotes> esNotesList = list.stream().map(entity -> {
            ESNotes esNotes = new ESNotes();
            BeanUtils.copyProperties(entity, esNotes);
            entity.setStoreEsTime(TimeConstants.DEFAULT_TIME);
            return esNotes;
        }).collect(Collectors.toList());

        List<ESNotes> embeddingNotes = new ArrayList<>();
        int batchSize = 4;
        for (ESNotes esNotes : esNotesList) {
            embeddingNotes.add(esNotes);
            if (embeddingNotes.size() == batchSize) {
                List<String> textList = embeddingNotes.stream().map(ESNotes::getContent).collect(Collectors.toList());
                List<List<Double>> embeddingList = embeddingsService.getEmbeddingList(textList);
                for (int i = 0; i < batchSize; i++) {
                    embeddingNotes.get(i).setEmbedding(embeddingList.get(i));
                }
                embeddingNotes.clear();
            }
        }
        if (!embeddingNotes.isEmpty()) {
            List<String> textList = embeddingNotes.stream().map(ESNotes::getContent).collect(Collectors.toList());
            List<List<Double>> embeddingList = embeddingsService.getEmbeddingList(textList);
            for (int i = 0; i < embeddingNotes.size(); i++) {
                embeddingNotes.get(i).setEmbedding(embeddingList.get(i));
            }
        }

        esNotesRepository.saveAll(esNotesList);
        if (!list.isEmpty()) {
            transactionTemplate.execute(status -> {
                this.updateBatchById(list);
                return null;
            });
        }
    }

    @Override
    public R<Page<IndexNotesListVO>> queryRelativeById(Long notesId, PageDTO page) {
        LambdaQueryWrapper<PersonalNotes> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PersonalNotes::getId, notesId);
        wrapper.eq(PersonalNotes::getNotesUserId, FrontSecurityUtils.getUserId());
        wrapper.eq(PersonalNotes::getRecycle, false);
        PersonalNotes notes = this.getOne(wrapper);
        if (notes == null) {
            return R.fail(null, "笔记不存在");
        }
        Optional<ESNotes> optional = esNotesRepository.findById(notesId.toString());
        if (!optional.isPresent()) {
            return R.fail(null, "笔记尚未构建完成");
        }

        Long userId = FrontSecurityUtils.getUserId();
        ESNotes esNotes = optional.get();
        SearchHits<ESNotes> similarNotesHits = findSimilarNotes(esNotes.getEmbedding(), userId, notesId.toString(), page.getCurrent(), page.getSize());
        List<IndexNotesListVO> resultContent = similarNotesHits.get().map(hit -> {
            IndexNotesListVO vo = new IndexNotesListVO();
            BeanUtils.copyProperties(hit.getContent(), vo);
            return vo;
        }).collect(Collectors.toList());

        return R.ok(PageUtils.page(resultContent, page.getCurrent(), resultContent.size(), similarNotesHits.getTotalHits()));

    }

    @Override
    public R<Page<IndexNotesListVO>> queryRelativeByKeyword(String keyword, PageDTO page) {
        List<List<Double>> embeddingList = embeddingsService.getEmbeddingList(List.of(keyword));
        SearchHits<ESNotes> similarNotesHits = findSimilarNotes(embeddingList.get(0), FrontSecurityUtils.getUserId(),
            null, page.getCurrent(), page.getSize());

        List<IndexNotesListVO> resultContent = similarNotesHits.get().map(hit -> {
            IndexNotesListVO vo = new IndexNotesListVO();
            BeanUtils.copyProperties(hit.getContent(), vo);
            return vo;
        }).collect(Collectors.toList());

        return R.ok(PageUtils.page(resultContent, page.getCurrent(), resultContent.size(), similarNotesHits.getTotalHits()));
    }

    @Override
    public R<Page<IndexNotesListVO>> queryRelativeByEsKeyword(String keyword, PageDTO pageDTO) {
        Long userId = FrontSecurityUtils.getUserId();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termQuery("notesUserId", userId));
        boolQuery.must(QueryBuilders.termQuery("recycle", false)); // 添加recycle字段必须为false的条件

        BoolQueryBuilder multiMatchBool = QueryBuilders.boolQuery();
        multiMatchBool.should(QueryBuilders.matchQuery("content", keyword).boost(4.0f));
        multiMatchBool.should(QueryBuilders.matchQuery("title", keyword).boost(3.0f));
        multiMatchBool.should(QueryBuilders.matchQuery("tag", keyword).boost(2.0f));
        multiMatchBool.should(QueryBuilders.matchQuery("source", keyword).boost(1.0f));

        boolQuery.must(multiMatchBool);

        HighlightBuilder.Field contentHighlight = new HighlightBuilder.Field("content")
            .preTags("<span style='background-color: yellow;'>").postTags("</span>");
        HighlightBuilder.Field titleHighlight = new HighlightBuilder.Field("title")
            .preTags("<span style='background-color: yellow;'>").postTags("</span>");

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(boolQuery)
            .withHighlightFields(contentHighlight, titleHighlight)
            .withPageable(PageRequest.of(pageDTO.getCurrent() - 1, pageDTO.getSize()))
            .build();

        SearchHits<ESNotes> searchHits = elasticsearchTemplate.search(searchQuery, ESNotes.class);

        List<IndexNotesListVO> resultContent = searchHits.get().map(hit -> {
            IndexNotesListVO vo = new IndexNotesListVO();
            BeanUtils.copyProperties(hit.getContent(), vo);

            // 处理高亮内容，直接拼接字符串列表
            if (hit.getHighlightFields().containsKey("content")) {
                List<String> fragments = hit.getHighlightFields().get("content");
                String highlightedContent = String.join("", fragments);
                vo.setContent(highlightedContent);
            }
            if (hit.getHighlightFields().containsKey("title")) {
                List<String> fragments = hit.getHighlightFields().get("title");
                String highlightedTitle = String.join("", fragments);
                vo.setTitle(highlightedTitle);
            }
            return vo;
        }).collect(Collectors.toList());

        return R.ok(PageUtils.page(resultContent, pageDTO.getCurrent(), resultContent.size(), searchHits.getTotalHits()));
    }*/

    @Override
    @Transactional
    public void recyclePersonalNotes(Long id) {
        PersonalNotes personalNotes = this.getById(id);
        personalNotes.setRecycle(true);
        personalNotes.setRecycleTime(LocalDateTime.now());
        this.updateById(personalNotes);
        /*Optional<ESNotes> optional = esNotesRepository.findById(id.toString());
         if (optional.isPresent()) {
            ESNotes esNotes = optional.get();
            esNotes.setRecycle(true);
            esNotesRepository.save(esNotes);
        }*/
    }

    @Override
    public Boolean addNotes(PersonalNotes personalNotes) {
        this.save(personalNotes);
        // 后续使用队列，或者微服务
        ragNotesService.storeNotes(personalNotes.getId());
        return true;
    }

    @Override
    public void updateNotes(PersonalNotes personalNotes) {
        this.updateById(personalNotes);
        // 后续使用队列，或者微服务
        ragNotesService.storeNotes(personalNotes.getId());
    }


    /*private SearchHits<ESNotes> findSimilarNotes(List<Double> embedding, Long notesUserId, String excludeId, int pageNum, int pageSize) {
        // 构造bool查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termQuery("notesUserId", notesUserId));
        boolQuery.must(QueryBuilders.termQuery("recycle", false));
        if (excludeId != null) {
            boolQuery.mustNot(QueryBuilders.termQuery("_id", excludeId));
        }

        // script_score查询计算向量相似度
        Map<String, Object> params = Collections.singletonMap("query_vector", embedding);
        Script script = new Script(ScriptType.INLINE, "painless",
            "cosineSimilarity(params.query_vector, 'embedding') + 1.0", params);

        ScriptScoreQueryBuilder scriptScoreQuery = QueryBuilders.scriptScoreQuery(boolQuery, script);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(scriptScoreQuery)
            .withPageable(PageRequest.of(pageNum - 1, pageSize))
            .withMinScore(1.8f)  // 设置相似度阈值，过滤低相似度结果
            .build();

        return elasticsearchTemplate.search(searchQuery, ESNotes.class);
    }*/
}
