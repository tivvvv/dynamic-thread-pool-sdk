package com.tiv.dynamicthreadpool.sdk.config;

import com.tiv.dynamicthreadpool.sdk.domain.DynamicThreadPoolConfigService;
import com.tiv.dynamicthreadpool.sdk.domain.impl.DynamicThreadPoolConfigServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 动态配置入口
 */
@Slf4j
@Configuration
public class DynamicThreadPoolAutoConfig {

    @Bean("dynamicThreadPoolConfigService")
    public DynamicThreadPoolConfigService dynamicThreadPoolConfigService(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");

        if (applicationName == null) {
            applicationName = "未命名";
            log.warn("工程未配置spring.application.name,默认为未命名");
        }

        return new DynamicThreadPoolConfigServiceImpl(applicationName, threadPoolExecutorMap);
    }
}