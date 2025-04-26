package com.notes.service.front.config.impl;

import com.notes.domain.front.config.KvConfig;
import com.notes.mapper.front.config.KvConfigMapper;
import com.notes.service.front.config.IKvConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 密钥配置 服务实现类
 * </p>
 *
 * @author ldj
 * @since 2025-04-25
 */
@Service
public class KvConfigServiceImpl extends ServiceImpl<KvConfigMapper, KvConfig> implements IKvConfigService {

}
