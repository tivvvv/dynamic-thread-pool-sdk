package com.tiv.dynamicthreadpool.sdk.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 线程池配置实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadPoolConfigEntity {

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 线程池名称
     */
    private String threadPoolName;

    /**
     * 核心线程数
     */
    private int corePoolSize;

    /**
     * 最大线程数
     */
    private int maximumPoolSize;

    /**
     * 当前活跃线程数
     */
    private int activeCount;

    /**
     * 当前池中线程数
     */
    private int poolSize;

    /**
     * 队列类型
     */
    private String queueType;

    /**
     * 当前队列任务数
     */
    private int queueSize;

    /**
     * 队列剩余任务数
     */
    private int remainingCapacity;

    public ThreadPoolConfigEntity(String applicationName, String threadPoolName) {
        this.applicationName = applicationName;
        this.threadPoolName = threadPoolName;
    }
}
