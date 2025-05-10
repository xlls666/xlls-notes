package com.notes.web.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.notes.common.exception.GlobalException;
import com.notes.common.utils.RestTemplateUtil;
import com.notes.frontframe.constants.KvConfigConstants;
import com.notes.frontframe.util.KvConfigUtils;
import com.notes.frontframe.web.service.TokenService;
import com.notes.domain.front.user.NotesUser;
import com.notes.service.front.user.INotesUserService;
import com.notes.web.service.WxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WxServiceImpl implements WxService {

    @Autowired
    private INotesUserService notesUserService;

    @Autowired
    private TokenService tokenService;

    @Override
    public String login(String code) {

        Map<String, String> params = new HashMap<>();
        params.put("appid", KvConfigUtils.getConfigValue(KvConfigConstants.WX_APPID));
        params.put("secret", KvConfigUtils.getConfigValue(KvConfigConstants.WX_SECRET));
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");
        String url = KvConfigUtils.getConfigValue(KvConfigConstants.WX_JSCODE2SESSION);
        ResponseEntity<String> response = RestTemplateUtil.getForObject(url, String.class, params);
        log.info("微信一键登录 {}，response:{}", code, response);
        if (response.getStatusCode().is2xxSuccessful()) {
            // 处理成功的响应
            String responseBody = response.getBody();
            JSONObject jsonObject = JSON.parseObject(responseBody);
            Object sessionKey = jsonObject.get("session_key");
            Object unionid = jsonObject.get("unionid");
            // 查询用户是否存在
            NotesUser user = notesUserService.lambdaQuery().eq(NotesUser::getWxUnionid, unionid).one();
            LocalDateTime now = LocalDateTime.now();
            if (user == null) {
                user = new NotesUser();
                user.setWxUnionid(unionid.toString());
                user.setLoginChannel(NotesUser.LoginChannel.WX_MINI_QUICK);
                user.setLastLoginTime(now);
                user.setLoginCount(1);
                user.setNickname(unionid.toString().substring(6,10));
                notesUserService.save(user);
            } else if (user.getStatus()) {
                user.setLoginCount(user.getLoginCount() + 1);
                user.setLastLoginTime(now);
                notesUserService.updateById(user);
            } else {
                throw new GlobalException("用户已被禁用");
            }
            // 生成token 必做
            return tokenService.createToken(user);
        } else {
            // 处理失败的响应
            throw new GlobalException(response.getBody());
        }
    }

    @Override
    public NotesUser test() {
        return notesUserService.getById(1);
    }
}
