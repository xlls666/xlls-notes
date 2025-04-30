package com.notes.web.service.notes.impl;

import com.notes.domain.front.notes.PersonalNotes;
import com.notes.mapper.front.notes.PersonalNotesMapper;
import com.notes.web.service.notes.IPersonalNotesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
