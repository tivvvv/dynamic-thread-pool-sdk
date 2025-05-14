package com.tiv.dynamicthreadpool.sdk.domain.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 注册中心key枚举
 */
@Getter
@AllArgsConstructor
public enum RegistryKeyEnum {

    THREAD_POOL_CONFIG_LIST_KEY("THREAD_POOL_CONFIG_LIST_KEY", "线程池配置列表"),
    THREAD_POOL_CONFIG_KEY("THREAD_POOL_CONFIG_KEY", "指定线程池配置"),
    DYNAMIC_THREAD_POOL_LISTENER_TOPIC("DYNAMIC_THREAD_POOL_LISTENER_TOPIC", "动态线程池监听主题");

    private final String key;
    private final String desc;
}
