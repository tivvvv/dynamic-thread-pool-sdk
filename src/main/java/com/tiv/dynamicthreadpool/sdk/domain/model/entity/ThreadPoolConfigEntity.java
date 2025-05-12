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
     * 应用名
     */
    private String appName;

    /**
     * 线程池名
     */
    private String threadPoolName;

    /**
     * 核心线程数
     */
    private int corePoolSize;

    /**
     * 最大线程数
     */
    private int maxPoolSize;

    /**
     * 活跃线程数
     */
    private int activeThreadCount;

    /**
     * 当前池中线程数
     */
    private int threadCount;

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
}