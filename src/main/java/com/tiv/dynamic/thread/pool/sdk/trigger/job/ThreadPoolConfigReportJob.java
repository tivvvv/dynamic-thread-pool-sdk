package com.tiv.dynamic.thread.pool.sdk.trigger.job;

import com.alibaba.fastjson.JSON;
import com.tiv.dynamic.thread.pool.sdk.domain.DynamicThreadPoolConfigService;
import com.tiv.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.tiv.dynamic.thread.pool.sdk.registry.RegistryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * 线程池配置上报任务
 */
@Slf4j
@AllArgsConstructor
public class ThreadPoolConfigReportJob {

    private final DynamicThreadPoolConfigService dynamicThreadPoolConfigService;

    private final RegistryService registryService;

    @Scheduled(cron = "0/30 * * * * ?")
    public void execute() {
        log.info("Thread pool config report job starts");

        List<ThreadPoolConfigEntity> threadPoolConfigEntityList = dynamicThreadPoolConfigService.queryThreadPoolConfigList();
        registryService.reportThreadPoolConfigList(threadPoolConfigEntityList);
        log.info("report thread pool config list: {}", JSON.toJSONString(threadPoolConfigEntityList));

        for (ThreadPoolConfigEntity threadPoolConfigEntity : threadPoolConfigEntityList) {
            registryService.reportThreadPoolConfig(threadPoolConfigEntity);
            log.info("report thread pool config: {}", JSON.toJSONString(threadPoolConfigEntity));
        }

        log.info("Thread pool config report job ends");
    }

}
