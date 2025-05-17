package com.tiv.dynamic.thread.pool.sdk.config;

import com.alibaba.fastjson.JSON;
import com.tiv.dynamic.thread.pool.sdk.domain.DynamicThreadPoolConfigService;
import com.tiv.dynamic.thread.pool.sdk.domain.impl.DynamicThreadPoolConfigServiceImpl;
import com.tiv.dynamic.thread.pool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.tiv.dynamic.thread.pool.sdk.domain.model.enums.RegistryKeyEnum;
import com.tiv.dynamic.thread.pool.sdk.registry.RegistryService;
import com.tiv.dynamic.thread.pool.sdk.registry.impl.RedisRegistryServiceImpl;
import com.tiv.dynamic.thread.pool.sdk.trigger.job.ThreadPoolConfigReportJob;
import com.tiv.dynamic.thread.pool.sdk.trigger.listener.ThreadPoolConfigAdjustListener;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 动态配置入口
 */
@Slf4j
@Configuration
@EnableScheduling
@EnableConfigurationProperties(DynamicThreadPoolRedisProperties.class)
@ConditionalOnProperty(name = "dynamic.thread.pool.config.enabled", havingValue = "true")
public class DynamicThreadPoolAutoConfig {

    private String applicationName;

    @Bean("dynamicThreadPoolConfigService")
    public DynamicThreadPoolConfigService dynamicThreadPoolConfigService(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap, RedissonClient redissonClient) {
        applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");

        if (applicationName == null) {
            applicationName = "unknown";
            log.warn("Application name not configured in spring.application.name, default value: unknown");
        }

        // 读取缓存中的线程池配置
        Set<String> threadPoolNames = threadPoolExecutorMap.keySet();
        for (String threadPoolName : threadPoolNames) {
            String cacheKey = String.format("%s_%s_%s", RegistryKeyEnum.THREAD_POOL_CONFIG_KEY.getKey(), applicationName, threadPoolName);
            ThreadPoolConfigEntity threadPoolConfigEntity = redissonClient.<ThreadPoolConfigEntity>getBucket(cacheKey).get();
            if (threadPoolConfigEntity == null) {
                continue;
            }
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolName);
            // 更新线程池配置
            threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaximumPoolSize());
            threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
        }
        log.info("Thread pool configs: {}", JSON.toJSONString(threadPoolExecutorMap));

        return new DynamicThreadPoolConfigServiceImpl(applicationName, threadPoolExecutorMap);
    }

    @Bean("dynamicThreadPoolRedissonClient")
    public RedissonClient dynamicThreadPoolRedissonClient(DynamicThreadPoolRedisProperties properties) {
        Config config = new Config();
        config.setCodec(JsonJacksonCodec.INSTANCE);

        String address = String.format("redis://%s:%s", properties.getHost(), properties.getPort());
        config.useSingleServer()
                .setAddress(address)
                .setConnectionPoolSize(properties.getPoolSize())
                .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                .setIdleConnectionTimeout(properties.getIdleTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setPingConnectionInterval(properties.getPingInterval())
                .setKeepAlive(properties.isKeepAlive());

        RedissonClient redissonClient = Redisson.create(config);
        log.info("Connected to redis registry successfully, config: {}", properties.toString());
        return redissonClient;
    }

    @Bean("redisRegistryServiceImpl")
    public RegistryService redisRegistryServiceImpl(RedissonClient redissonClient) {
        return new RedisRegistryServiceImpl(redissonClient);
    }

    @Bean("threadPoolConfigReportJob")
    public ThreadPoolConfigReportJob threadPoolConfigReportJob(DynamicThreadPoolConfigService dynamicThreadPoolService, RegistryService registryService) {
        return new ThreadPoolConfigReportJob(dynamicThreadPoolService, registryService);
    }

    @Bean("threadPoolConfigAdjustListener")
    public ThreadPoolConfigAdjustListener threadPoolConfigAdjustListener(DynamicThreadPoolConfigService dynamicThreadPoolService, RegistryService registryService) {
        return new ThreadPoolConfigAdjustListener(dynamicThreadPoolService, registryService);
    }

    @Bean(name = "dynamicThreadPoolListenerTopic")
    public RTopic dynamicThreadPoolListenerTopic(RedissonClient redissonClient, ThreadPoolConfigAdjustListener threadPoolConfigAdjustListener) {
        RTopic topic = redissonClient.getTopic(String.format("%s_%s", RegistryKeyEnum.DYNAMIC_THREAD_POOL_LISTENER_TOPIC.getKey(), applicationName));
        topic.addListener(ThreadPoolConfigEntity.class, threadPoolConfigAdjustListener);
        return topic;
    }

}