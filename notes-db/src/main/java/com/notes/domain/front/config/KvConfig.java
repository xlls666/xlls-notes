package com.notes.domain.front.config;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 密钥配置
 * </p>
 *
 * @author ldj
 * @since 2025-04-25
 */
@Data
@TableName("kv_config")
public class KvConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 键
     */
    @TableField("config_key")
    private String configKey;

    /**
     * 值
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 是否删除
     */
    @TableField("del")
    @TableLogic
    private Byte del;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

}
