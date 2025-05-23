package com.tiv.dynamic.thread.pool.sdk.registry.impl;

import com.tiv.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.tiv.dynamic.thread.pool.sdk.domain.model.enums.RegistryKeyEnum;
import com.tiv.dynamic.thread.pool.sdk.registry.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.List;

/**
 * redis注册中心服务实现
 */
@Slf4j
public class RedisRegistryServiceImpl implements RegistryService {

    private final RedissonClient redissonClient;

    public RedisRegistryServiceImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void reportThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity) {
        try {
            String cacheKey = String.format("%s_%s_%s", RegistryKeyEnum.THREAD_POOL_CONFIG_KEY.getKey(),
                    threadPoolConfigEntity.getApplicationName(), threadPoolConfigEntity.getThreadPoolName());
            RBucket<ThreadPoolConfigEntity> bucket = redissonClient.getBucket(cacheKey);
            bucket.set(threadPoolConfigEntity, Duration.ofDays(30));
        } catch (Exception e) {
            log.error("Failed to report thread pool config to redis registry", e);
        }
    }

    @Override
    public void reportThreadPoolConfigList(List<ThreadPoolConfigEntity> threadPoolConfigEntityList) {
        try {
            String cacheKey = RegistryKeyEnum.THREAD_POOL_CONFIG_LIST_KEY.getKey();
            RList<ThreadPoolConfigEntity> list = redissonClient.getList(cacheKey);
            list.delete();
            list.addAll(threadPoolConfigEntityList);
        } catch (Exception e) {
            log.error("Failed to report thread pool config list to redis registry", e);
        }
    }

}
