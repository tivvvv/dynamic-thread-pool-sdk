package com.tiv.dynamicthreadpool.sdk.domain.impl;

import com.tiv.dynamicthreadpool.sdk.domain.DynamicThreadPoolConfigService;
import com.tiv.dynamicthreadpool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 动态线程池配置服务实现
 */
@Slf4j
@AllArgsConstructor
public class DynamicThreadPoolConfigServiceImpl implements DynamicThreadPoolConfigService {

    private final String applicationName;

    private final Map<String, ThreadPoolExecutor> threadPoolExecutorMap;

    @Override
    public List<ThreadPoolConfigEntity> queryThreadPoolConfigList() {
        return Collections.emptyList();
    }

    @Override
    public ThreadPoolConfigEntity queryThreadPoolConfigByName(String threadPoolName) {
        return null;
    }

    @Override
    public void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity) {

    }
}
