package com.tiv.dynamic.thread.pool.sdk.domain.impl;

import com.alibaba.fastjson.JSON;
import com.tiv.dynamic.thread.pool.sdk.domain.DynamicThreadPoolConfigService;
import com.tiv.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        Set<String> threadPoolExecutorNames = threadPoolExecutorMap.keySet();
        List<ThreadPoolConfigEntity> threadPoolConfigEntityList = new ArrayList<>(threadPoolExecutorNames.size());
        for (String threadPoolExecutorName : threadPoolExecutorNames) {
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolExecutorName);
            ThreadPoolConfigEntity threadPoolConfigEntity = ThreadPoolConfigEntity.builder()
                    .applicationName(applicationName)
                    .threadPoolName(threadPoolExecutorName)
                    .corePoolSize(threadPoolExecutor.getCorePoolSize())
                    .maximumPoolSize(threadPoolExecutor.getMaximumPoolSize())
                    .activeCount(threadPoolExecutor.getActiveCount())
                    .poolSize(threadPoolExecutor.getPoolSize())
                    .queueType(threadPoolExecutor.getQueue().getClass().getSimpleName())
                    .queueSize(threadPoolExecutor.getQueue().size())
                    .remainingCapacity(threadPoolExecutor.getQueue().remainingCapacity())
                    .build();
            threadPoolConfigEntityList.add(threadPoolConfigEntity);
        }
        return threadPoolConfigEntityList;
    }

    @Override
    public ThreadPoolConfigEntity queryThreadPoolConfigByName(String threadPoolName) {
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolName);
        if (threadPoolExecutor == null) {
            return new ThreadPoolConfigEntity(applicationName, threadPoolName);
        }
        ThreadPoolConfigEntity threadPoolConfigEntity = ThreadPoolConfigEntity.builder()
                .applicationName(applicationName)
                .threadPoolName(threadPoolName)
                .corePoolSize(threadPoolExecutor.getCorePoolSize())
                .maximumPoolSize(threadPoolExecutor.getMaximumPoolSize())
                .activeCount(threadPoolExecutor.getActiveCount())
                .poolSize(threadPoolExecutor.getPoolSize())
                .queueType(threadPoolExecutor.getQueue().getClass().getSimpleName())
                .queueSize(threadPoolExecutor.getQueue().size())
                .remainingCapacity(threadPoolExecutor.getQueue().remainingCapacity())
                .build();
        log.info("Thread pool config, application: {}, pool: {}, detail: {}", applicationName, threadPoolName, JSON.toJSONString(threadPoolConfigEntity));
        return threadPoolConfigEntity;
    }

    @Override
    public void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity) {
        if (threadPoolConfigEntity == null || !applicationName.equals(threadPoolConfigEntity.getApplicationName())) {
            return;
        }

        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolConfigEntity.getThreadPoolName());
        if (threadPoolExecutor == null) {
            return;
        }

        int corePoolSize = threadPoolConfigEntity.getCorePoolSize();
        int maximumPoolSize = threadPoolConfigEntity.getMaximumPoolSize();
        if (corePoolSize > maximumPoolSize) {
            log.error("Invalid thread pool config: corePoolSize [{}] cannot be greater than maximumPoolSize [{}]",
                    corePoolSize, maximumPoolSize);
            return;
        }

        if (corePoolSize > threadPoolExecutor.getMaximumPoolSize()) {
            threadPoolExecutor.setMaximumPoolSize(maximumPoolSize);
            threadPoolExecutor.setCorePoolSize(corePoolSize);
        } else {
            threadPoolExecutor.setCorePoolSize(corePoolSize);
            threadPoolExecutor.setMaximumPoolSize(maximumPoolSize);
        }
    }

}
