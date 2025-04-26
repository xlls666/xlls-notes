package com.notes.frontframe.util;

import com.notes.common.constant.HttpStatus;
import com.notes.common.exception.ServiceException;
import com.notes.frontframe.model.FrontLoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全服务工具类
 * 
 * @author ruoyi
 */
public class FrontSecurityUtils
{

    /**
     * 用户ID
     **/
    public static Long getUserId()
    {
        try
        {
            return getLoginUser().getId();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取用户ID异常", HttpStatus.UNAUTHORIZED);
        }
    }


    /**
     * 获取用户
     **/
    public static FrontLoginUser getLoginUser()
    {
        try
        {
            return (FrontLoginUser) getAuthentication().getPrincipal();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取用户信息异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
