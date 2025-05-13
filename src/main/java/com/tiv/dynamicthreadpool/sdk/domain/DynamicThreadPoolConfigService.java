package com.tiv.dynamicthreadpool.sdk.domain;

import com.tiv.dynamicthreadpool.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * 动态线程池配置服务
 */
public interface DynamicThreadPoolConfigService {

    /**
     * 查询线程池配置列表
     *
     * @return
     */
    List<ThreadPoolConfigEntity> queryThreadPoolConfigList();

    /**
     * 查询指定线程池配置
     *
     * @param threadPoolName
     * @return
     */
    ThreadPoolConfigEntity queryThreadPoolConfigByName(String threadPoolName);

    /**
     * 更新指定线程池配置
     *
     * @param threadPoolConfigEntity
     */
    void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity);
}