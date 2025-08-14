package com.notes.web.service.notes;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.notes.common.core.domain.R;
import com.notes.domain.front.notes.PersonalNotes;
import com.baomidou.mybatisplus.extension.service.IService;
import com.notes.web.pojo.dto.base.PageDTO;
import com.notes.web.pojo.vo.notes.IndexNotesListVO;

/**
 * <p>
 * 个人笔记表 服务类
 * </p>
 *
 * @author ldj
 * @since 2025-04-30
 */
public interface IPersonalNotesService extends IService<PersonalNotes> {

    /*void updateEs();

    R<Page<IndexNotesListVO>> queryRelativeById(Long notesId, PageDTO pageDTO);

    R<Page<IndexNotesListVO>> queryRelativeByKeyword(String keyword, PageDTO pageDTO);

    R<Page<IndexNotesListVO>> queryRelativeByEsKeyword(String keyword, PageDTO pageDTO);*/

    void recyclePersonalNotes(Long id);

    Boolean addNotes(PersonalNotes personalNotes);

    void updateNotes(PersonalNotes personalNotes);
}
