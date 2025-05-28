package com.notes.web.controller.auth;

import com.notes.common.constant.Constants;
import com.notes.common.core.domain.AjaxResult;
import com.notes.frontframe.model.FrontLoginUser;
import com.notes.frontframe.util.FrontSecurityUtils;
import com.notes.web.service.wx.WxService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Api(tags = "登录认证")
public class AuthController {
    @Autowired
    private WxService wxService;

    @ApiOperation("微信小程序一键登录")
    @PostMapping("/login/wx-mini/quick/{code}")
    public AjaxResult wxMiniQuickLogin(@PathVariable String code) {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = wxService.login(code);
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/getInfo")
    public AjaxResult getInfo()
    {
        FrontLoginUser loginUser = FrontSecurityUtils.getLoginUser();
//        SysUser user = loginUser.getUser();
//        // 角色集合
//        Set<String> roles = permissionService.getRolePermission(user);
//        // 权限集合
//        Set<String> permissions = permissionService.getMenuPermission(user);
//        if (!loginUser.getPermissions().equals(permissions))
//        {
//            loginUser.setPermissions(permissions);
//            tokenService.refreshToken(loginUser);
//        }
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", loginUser);
        ajax.put("roles", null);
        ajax.put("permissions", null);
        return ajax;
    }
}
