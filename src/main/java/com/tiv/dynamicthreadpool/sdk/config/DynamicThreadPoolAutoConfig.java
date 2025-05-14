package com.tiv.dynamicthreadpool.sdk.config;

import com.tiv.dynamicthreadpool.sdk.domain.DynamicThreadPoolConfigService;
import com.tiv.dynamicthreadpool.sdk.domain.impl.DynamicThreadPoolConfigServiceImpl;
import com.tiv.dynamicthreadpool.sdk.domain.model.entity.ThreadPoolConfigEntity;
import com.tiv.dynamicthreadpool.sdk.domain.model.enums.RegistryKeyEnum;
import com.tiv.dynamicthreadpool.sdk.registry.RegistryService;
import com.tiv.dynamicthreadpool.sdk.registry.impl.RedisRegistryServiceImpl;
import com.tiv.dynamicthreadpool.sdk.trigger.job.ThreadPoolConfigReportJob;
import com.tiv.dynamicthreadpool.sdk.trigger.listener.ThreadPoolConfigAdjustListener;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 动态配置入口
 */
@Slf4j
@Configuration
@EnableScheduling
@EnableConfigurationProperties(DynamicThreadPoolRedisProperties.class)
public class DynamicThreadPoolAutoConfig {

    private String applicationName;

    @Bean("dynamicThreadPoolConfigService")
    public DynamicThreadPoolConfigService dynamicThreadPoolConfigService(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");

        if (applicationName == null) {
            applicationName = "unnamed";
            log.warn("Application name not configured in spring.application.name, default value: unnamed");
        }

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