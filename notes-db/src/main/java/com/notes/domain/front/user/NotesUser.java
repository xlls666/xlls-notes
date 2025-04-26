package com.notes.domain.front.user;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author ldj
 * @since 2025-04-16
 */
@Getter
@Setter
@TableName("notes_user")
public class NotesUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 微信unionid
     */
    @TableField("wx_unionid")
    private String WxUnionid;

    /**
     * 用户状态 (1: 启用, 0: 禁用)
     */
    @TableField("status")
    private Boolean status;

    /**
     * 软删除标志 (1: 已删除, 0: 未删除)
     */
    @TableField("del")
    @TableLogic
    private Boolean del;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 头像
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 性别 (1: 男, 2: 女, 0: 未知)
     */
    @TableField("gender")
    private Gender gender;

    /**
     * 角色ID
     */
    @TableField("role_id")
    private Byte roleId;

    /**
     * 权限列表
     */
    @TableField("permissions")
    private String permissions;

    /**
     * 登录渠道 (1: 微信一键登录)
     */
    @TableField("login_channel")
    private LoginChannel loginChannel;

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 登录次数
     */
    @TableField("login_count")
    private Integer loginCount;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    @Getter
    @AllArgsConstructor
    public enum Gender {
        MALE((byte) 1),
        FEMALE((byte) 2),
        UNKNOWN((byte) 0);

        @EnumValue // 标记数据库存的值是code
        private final Byte code;
    }

    @Getter
    @AllArgsConstructor
    public enum LoginChannel {
        WX_MINI_QUICK((byte) 1);

        @EnumValue // 标记数据库存的值是code
        private final Byte code;
    }

}
