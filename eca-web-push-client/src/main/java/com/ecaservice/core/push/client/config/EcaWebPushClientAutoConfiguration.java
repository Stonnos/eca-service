package com.ecaservice.core.push.client.config;

import com.ecaservice.core.push.client.service.WebPushClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Eca web client client configuration class.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@EnableAsync
@EnableConfigurationProperties(EcaWebPushClientProperties.class)
@ComponentScan({"com.ecaservice.core.push.client"})
@EnableFeignClients(basePackageClasses = WebPushClient.class)
@ConditionalOnProperty(value = "web-push.client.enabled", havingValue = "true")
public class EcaWebPushClientAutoConfiguration {

    /**
     * Mail client thread pool task executor bean
     */
    public static final String WEB_PUSH_CLIENT_THREAD_POOL_TASK_EXECUTOR = "webPushClientThreadPoolTaskExecutor";

    /**
     * Creates thread pool task executor bean.
     *
     * @param ecaWebPushClientProperties - eca web push client properties
     * @return thread pool task executor
     */
    @Bean(name = WEB_PUSH_CLIENT_THREAD_POOL_TASK_EXECUTOR)
    @ConditionalOnProperty(value = "web-push.client.async", havingValue = "true")
    public Executor webPushClientEventThreadPoolTaskExecutor(EcaWebPushClientProperties ecaWebPushClientProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(ecaWebPushClientProperties.getThreadPoolSize());
        executor.setMaxPoolSize(ecaWebPushClientProperties.getThreadPoolSize());
        return executor;
    }
}
