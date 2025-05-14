package com.tiv.dynamicthreadpool.sdk.registry.impl;

import com.tiv.dynamicthreadpool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.tiv.dynamicthreadpool.sdk.domain.model.enums.RegistryKeyEnum;
import com.tiv.dynamicthreadpool.sdk.registry.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.util.List;

/**
 * Redis注册中心服务实现
 */
@Slf4j
public class RedisRegistryServiceImpl implements RegistryService {

    private final RedissonClient redissonClient;

    public RedisRegistryServiceImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void reportThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity) {

    }

    @Override
    public void reportThreadPoolConfigList(List<ThreadPoolConfigEntity> threadPoolConfigEntityList) {
        try {
            String cacheKey = RegistryKeyEnum.THREAD_POOL_CONFIG_LIST_KEY.getKey();
            RList<ThreadPoolConfigEntity> list = redissonClient.getList(cacheKey);
            list.delete();
            list.addAll(threadPoolConfigEntityList);
        } catch (Exception e) {
            log.error("Failed to report thread pool config list to Redis registry", e);
        }
    }
}
