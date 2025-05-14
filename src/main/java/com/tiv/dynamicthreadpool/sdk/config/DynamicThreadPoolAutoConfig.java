package com.tiv.dynamicthreadpool.sdk.config;

import com.tiv.dynamicthreadpool.sdk.domain.DynamicThreadPoolConfigService;
import com.tiv.dynamicthreadpool.sdk.domain.impl.DynamicThreadPoolConfigServiceImpl;
import com.tiv.dynamicthreadpool.sdk.registry.RegistryService;
import com.tiv.dynamicthreadpool.sdk.registry.impl.RedisRegistryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(DynamicThreadPoolRedisProperties.class)
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
        log.info("动态线程池连接redis注册中心成功,配置信息:{}", properties.toString());
        return redissonClient;
    }

    @Bean("redisRegistryServiceImpl")
    public RegistryService redisRegistryServiceImpl(RedissonClient redissonClient) {
        return new RedisRegistryServiceImpl(redissonClient);
    }
}