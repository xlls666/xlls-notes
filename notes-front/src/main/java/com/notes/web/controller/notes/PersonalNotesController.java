package com.notes.web.controller.notes;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notes.common.core.domain.R;
import com.notes.common.utils.PageUtils;
import com.notes.common.utils.bean.BeanUtils;
import com.notes.domain.front.notes.PersonalNotes;
import com.notes.frontframe.util.FrontSecurityUtils;
import com.notes.web.pojo.dto.notes.AddPersonalNotesDTO;
import com.notes.web.pojo.dto.notes.EditPersonalNotesDTO;
import com.notes.web.pojo.dto.notes.QueryPersonalNotesDTO;
import com.notes.web.pojo.vo.notes.IndexNotesListVO;
import com.notes.web.service.notes.IPersonalNotesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
public class PersonalNotesController {
    @Autowired
    private IPersonalNotesService personalNotesService;

    /* 笔记关联网 */


    /* 个人笔记简单增删改查 */
    @PutMapping("/recycle/{id}")
    @ApiOperation("删除个人笔记至回收站")
    public R<Boolean> delete(@PathVariable("id") Long id) {
        PersonalNotes personalNotes = personalNotesService.getById(id);
        personalNotes.setRecycle(true);
        personalNotes.setRecycleTime(LocalDateTime.now());
        return R.ok(personalNotesService.updateById(personalNotes));
    }

    @GetMapping("detail/{id}")
    @ApiOperation("查询个人笔记详情")
    public R<PersonalNotes> detail(@PathVariable("id") Long id) {
        PersonalNotes personalNotes = personalNotesService.getById(id);
        return R.ok(personalNotes);
    }

    @PutMapping("/update")
    @ApiOperation("修改个人笔记")
    public R<Boolean> update(@RequestBody EditPersonalNotesDTO editDTO) {
        PersonalNotes personalNotes = new PersonalNotes();
        BeanUtils.copyProperties(editDTO, personalNotes);
        personalNotes.setUpdateTime(LocalDateTime.now());
        return R.ok(personalNotesService.updateById(personalNotes));
    }

    @PostMapping("/add")
    @ApiOperation("新增个人笔记")
    public R<Boolean> add(@RequestBody AddPersonalNotesDTO addDTO) {
        PersonalNotes personalNotes = new PersonalNotes();
        personalNotes.addInit();
        BeanUtils.copyProperties(addDTO, personalNotes);
        Long userId = FrontSecurityUtils.getUserId();
        personalNotes.setNotesUserId(userId);
        return R.ok(personalNotesService.save(personalNotes));
    }

    @GetMapping("list")
    @ApiOperation("查询个人笔记列表")
    public R<Page<IndexNotesListVO>> list(QueryPersonalNotesDTO queryDTO) {
        LambdaQueryWrapper<PersonalNotes> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PersonalNotes::getNotesUserId, FrontSecurityUtils.getUserId());
        queryWrapper.eq(PersonalNotes::getRecycle, queryDTO.getRecycle());
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
