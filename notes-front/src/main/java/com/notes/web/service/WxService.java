package com.notes.web.service;

import com.notes.domain.front.user.NotesUser;

public interface WxService {
    String login(String code);

    NotesUser test();

}
