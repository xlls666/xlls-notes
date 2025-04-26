package com.notes.frontframe.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.notes.common.utils.spring.SpringUtils;
import com.notes.domain.front.config.KvConfig;
import com.notes.mapper.front.config.KvConfigMapper;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
public class KvConfigUtils {
    private final static Map<String, String> config = new HashMap<>();

    static {
        KvConfigMapper configMapper = SpringUtils.getBean(KvConfigMapper.class);
        LambdaQueryWrapper<KvConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KvConfig::getDel, 0);
        configMapper.selectList(queryWrapper)
            .forEach(kvConfig -> config.put(kvConfig.getConfigKey(), kvConfig.getConfigValue()));
    }

    public static String getConfigValue(String key) {
        return config.get(key);
    }
}
