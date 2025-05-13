package com.tiv.dynamicthreadpool.sdk.registry.impl;

import com.tiv.dynamicthreadpool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.tiv.dynamicthreadpool.sdk.registry.RegistryService;
import org.redisson.api.RedissonClient;

import java.util.List;

/**
 * Redis注册中心服务实现
 */
public class RedisRegistryServiceImpl implements RegistryService {

    private final RedissonClient redissonClient;

    public RedisRegistryServiceImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void reportThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity) {

    }

    @Override
    public void reportAllThreadPoolConfigs(List<ThreadPoolConfigEntity> threadPoolConfigEntityList) {

    }
}
