package com.tiv.dynamic.thread.pool.sdk.trigger.listener;

import com.tiv.dynamic.thread.pool.sdk.domain.DynamicThreadPoolConfigService;
import com.tiv.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.tiv.dynamic.thread.pool.sdk.registry.RegistryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.listener.MessageListener;

import java.util.List;

/**
 * 线程池配置调整监听器
 */
@Slf4j
@AllArgsConstructor
public class ThreadPoolConfigAdjustListener implements MessageListener<ThreadPoolConfigEntity> {

    private final DynamicThreadPoolConfigService dynamicThreadPoolConfigService;

    private final RegistryService registryService;

    @Override
    public void onMessage(CharSequence charSequence, ThreadPoolConfigEntity threadPoolConfigEntity) {
        log.info("Thread pool config adjust listener detected: {}", threadPoolConfigEntity.toString());
        dynamicThreadPoolConfigService.updateThreadPoolConfig(threadPoolConfigEntity);

        // 调整配置后重新上报数据
        List<ThreadPoolConfigEntity> threadPoolConfigEntityList = dynamicThreadPoolConfigService.queryThreadPoolConfigList();
        registryService.reportThreadPoolConfigList(threadPoolConfigEntityList);

        ThreadPoolConfigEntity threadPoolConfigEntityUpdated = dynamicThreadPoolConfigService.queryThreadPoolConfigByName(threadPoolConfigEntity.getThreadPoolName());
        registryService.reportThreadPoolConfig(threadPoolConfigEntityUpdated);
    }

}
