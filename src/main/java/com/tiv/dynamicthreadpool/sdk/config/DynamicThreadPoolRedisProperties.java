package com.tiv.dynamicthreadpool.sdk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 动态线程池redis配置属性
 */
@Data
@ConfigurationProperties(prefix = "dynamic.thread.pool.config", ignoreInvalidFields = true)
public class DynamicThreadPoolRedisProperties {

    /**
     * redis host
     */
    private String host;

    /**
     * redis port
     */
    private int port;

    /**
     * 连接池的大小
     */
    private int poolSize = 64;

    /**
     * 连接池的最小空闲连接数
     */
    private int minIdleSize = 10;

    /**
     * 连接的最大空闲时间/ms
     */
    private int idleTimeout = 10000;

    /**
     * 连接超时时间/ms
     */
    private int connectTimeout = 10000;

    /**
     * 连接重试次数
     */
    private int retryAttempts = 3;

    /**
     * 连接重试间隔/ms
     */
    private int retryInterval = 1000;

    /**
     * 定期检查连接可用性间隔/ms
     */
    private int pingInterval = 0;

    /**
     * 是否保持长连接
     */
    private boolean keepAlive = true;

}
