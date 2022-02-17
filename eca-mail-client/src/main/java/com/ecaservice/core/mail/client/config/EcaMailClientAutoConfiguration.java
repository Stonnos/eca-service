package com.ecaservice.core.mail.client.config;

import com.ecaservice.common.web.crypto.EncryptorBase64AdapterService;
import com.ecaservice.core.mail.client.service.EmailClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;

import java.util.concurrent.Executor;

import static com.ecaservice.common.web.crypto.factory.EncryptFactory.getAesBytesEncryptor;

/**
 * Eca mail client configuration class.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@EnableAsync
@EnableConfigurationProperties(EcaMailClientProperties.class)
@ComponentScan({"com.ecaservice.core.mail.client"})
@EnableFeignClients(basePackageClasses = EmailClient.class)
@ConditionalOnProperty(value = "mail.client.enabled", havingValue = "true")
public class EcaMailClientAutoConfiguration {

    /**
     * Mail client thread pool task executor bean
     */
    public static final String MAIL_CLIENT_THREAD_POOL_TASK_EXECUTOR = "mailClientThreadPoolTaskExecutor";

    /**
     * Creates thread pool task executor bean.
     *
     * @param ecaMailClientProperties - eca mail client properties
     * @return thread pool task executor
     */
    @Bean(name = MAIL_CLIENT_THREAD_POOL_TASK_EXECUTOR)
    @ConditionalOnProperty(value = "mail.client.async", havingValue = "true")
    public Executor mailClientEventThreadPoolTaskExecutor(EcaMailClientProperties ecaMailClientProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(ecaMailClientProperties.getThreadPoolSize());
        executor.setMaxPoolSize(ecaMailClientProperties.getThreadPoolSize());
        return executor;
    }

    /**
     * Creates AES encryptor bean.
     *
     * @param ecaMailClientProperties - eca mail properties
     * @return AES encryptor bean
     */
    @Bean
    public AesBytesEncryptor aesBytesEncryptor(EcaMailClientProperties ecaMailClientProperties) {
        return getAesBytesEncryptor(ecaMailClientProperties.getEncrypt().getPassword(),
                ecaMailClientProperties.getEncrypt().getSalt());
    }

    /**
     * Creates encryptor Base64 adapter service bean.
     *
     * @param aesBytesEncryptor - AES encryptor bean
     * @return encryptor Base64 adapter service bean
     */
    @Bean
    public EncryptorBase64AdapterService encryptorBase64AdapterService(AesBytesEncryptor aesBytesEncryptor) {
        return new EncryptorBase64AdapterService(aesBytesEncryptor);
    }
}
