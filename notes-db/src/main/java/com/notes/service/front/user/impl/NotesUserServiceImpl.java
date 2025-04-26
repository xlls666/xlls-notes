package com.notes.service.front.user.impl;


import com.notes.domain.front.user.NotesUser;
import com.notes.mapper.front.user.NotesUserMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.notes.service.front.user.INotesUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author ldj
 * @since 2025-04-16
 */

@Service
public class NotesUserServiceImpl extends ServiceImpl<NotesUserMapper, NotesUser> implements INotesUserService {

}
