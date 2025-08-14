package com.notes.web.controller.notes;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notes.common.core.domain.R;
import com.notes.common.utils.PageUtils;
import com.notes.common.utils.bean.BeanUtils;
import com.notes.domain.front.notes.PersonalNotes;
import com.notes.frontframe.util.FrontSecurityUtils;
import com.notes.web.pojo.dto.base.PageDTO;
import com.notes.web.pojo.dto.notes.AddPersonalNotesDTO;
import com.notes.web.pojo.dto.notes.EditPersonalNotesDTO;
import com.notes.web.pojo.dto.notes.QueryPersonalNotesDTO;
import com.notes.web.pojo.vo.notes.IndexNotesListVO;
import com.notes.web.service.notes.IPersonalNotesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 个人笔记表 前端控制器
 * </p>
 *
 * @author ldj
 * @since 2025-04-30
 */
@RestController
@Api(tags = "个人笔记")
@RequestMapping("/personal-notes")
@Validated
public class PersonalNotesController {
    @Autowired
    private IPersonalNotesService personalNotesService;


    /* es个人笔记(已废弃) */
    /*@PostMapping("/update-es")
    @ApiOperation("更新个人笔记es")
    public R updateEs() {
        personalNotesService.updateEs();
        return R.ok();
    }

    @GetMapping("relative/ai-id")
    @ApiOperation("根据id，语义查询关联笔记")
    public R<Page<IndexNotesListVO>> queryRelativeById(Long notesId, PageDTO pageDTO) {
        return personalNotesService.queryRelativeById(notesId, pageDTO);
    }

    @GetMapping("relative/ai-keyword")
    @ApiOperation("根据关键词，语义查询关联笔记")
    public R<Page<IndexNotesListVO>> queryRelativeByKeyword(@NotBlank(message = "关键词不能为空") String keyword, PageDTO pageDTO) {
        return personalNotesService.queryRelativeByKeyword(keyword, pageDTO);
    }

    @GetMapping("relative/es-keyword")
    @ApiOperation("根据关键词，es查询关联笔记")
    public R<Page<IndexNotesListVO>> queryRelativeByEsKeyword(@NotBlank(message = "关键词不能为空") String keyword, PageDTO pageDTO) {
        return personalNotesService.queryRelativeByEsKeyword(keyword, pageDTO);
    }*/

    /* 个人笔记简单增删改查 */
    @PutMapping("/recycle/{id}")
    @ApiOperation("删除个人笔记至回收站")
    public R delete(@PathVariable("id") Long id) {
        personalNotesService.recyclePersonalNotes(id);
        return R.ok();
    }

    @GetMapping("detail/{id}")
    @ApiOperation("查询个人笔记详情")
    public R<PersonalNotes> detail(@PathVariable("id") Long id) {
        PersonalNotes personalNotes = personalNotesService.getById(id);
        return R.ok(personalNotes);
    }

    @PutMapping("/update")
    @ApiOperation("修改个人笔记")
    public R<Boolean> update(@RequestBody @Valid EditPersonalNotesDTO editDTO) {
        PersonalNotes personalNotes = new PersonalNotes();
        BeanUtils.copyProperties(editDTO, personalNotes);
        personalNotes.setUpdateTime(LocalDateTime.now());
        if (editDTO.getContent() != null && editDTO.getContent().length() > 10) {
            personalNotes.setStoreEsTime(LocalDateTime.now());
        }
        personalNotesService.updateNotes(personalNotes);
        return R.ok(true);
    }

    @PostMapping("/add")
    @ApiOperation("新增个人笔记")
    public R<Boolean> add(@RequestBody @Valid AddPersonalNotesDTO addDTO) {
        PersonalNotes personalNotes = new PersonalNotes();
        personalNotes.addInit();
        BeanUtils.copyProperties(addDTO, personalNotes);
        Long userId = FrontSecurityUtils.getUserId();
        personalNotes.setNotesUserId(userId);

        return R.ok(personalNotesService.addNotes(personalNotes));
    }

    @GetMapping("list")
    @ApiOperation("查询个人笔记列表")
    public R<Page<IndexNotesListVO>> list(QueryPersonalNotesDTO queryDTO) {
        LambdaQueryWrapper<PersonalNotes> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PersonalNotes::getNotesUserId, FrontSecurityUtils.getUserId());
        queryWrapper.eq(PersonalNotes::getRecycle, queryDTO.getRecycle());
        
        if (StringUtils.isNotBlank(queryDTO.getKeyword())) {
            // 将以下四个查询合并为为or查询，和上述的两个条件and并列
            queryWrapper.and(wrapper -> wrapper
                .like(PersonalNotes::getContent, queryDTO.getKeyword())
                .or()
                .like(PersonalNotes::getTitle, queryDTO.getKeyword())
                .or()
                .like(PersonalNotes::getTag, queryDTO.getKeyword())
                .or()
                .like(PersonalNotes::getSource, queryDTO.getKeyword()));
        }
        
        queryWrapper.orderByDesc(PersonalNotes::getId);
        Page<PersonalNotes> pageResult = personalNotesService.page(
            new Page<>(queryDTO.getCurrent(), queryDTO.getSize()), queryWrapper);

        List<IndexNotesListVO> records = pageResult.getRecords().stream().map(item -> {
            IndexNotesListVO vo = new IndexNotesListVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());

        return R.ok(PageUtils.parseVOPage(pageResult, records));
    }
}
