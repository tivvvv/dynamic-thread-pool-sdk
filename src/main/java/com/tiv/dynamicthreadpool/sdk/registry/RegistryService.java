package com.tiv.dynamicthreadpool.sdk.registry;

import com.tiv.dynamicthreadpool.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * 注册中心服务
 */
public interface RegistryService {

    /**
     * 上报指定线程池配置
     *
     * @param threadPoolConfigEntity
     */
    void reportThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity);

    /**
     * 上报线程池配置列表
     *
     * @param threadPoolConfigEntityList
     */
    void reportThreadPoolConfigList(List<ThreadPoolConfigEntity> threadPoolConfigEntityList);

}
