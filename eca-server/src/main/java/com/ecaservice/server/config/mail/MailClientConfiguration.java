package com.ecaservice.server.config.mail;

import com.ecaservice.core.lock.redis.config.RedisLockProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

import static com.ecaservice.core.mail.client.config.EcaMailClientAutoConfiguration.MAIL_LOCK_REGISTRY;

/**
 * Mail client configuration class.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
public class MailClientConfiguration {

    private static final String REGISTRY_PREFIX = "%s-mail-client";

    /**
     * Creates redis lock registry for eca mail client.
     *
     * @param redisConnectionFactory - redis connection factory
     * @return eca mil client redis lock registry
     */
    @Bean(MAIL_LOCK_REGISTRY)
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory,
                                               RedisLockProperties redisLockProperties) {
        String registryKey = String.format(REGISTRY_PREFIX, redisLockProperties.getRegistryKey());
        var redisLockRegistry =
                new RedisLockRegistry(redisConnectionFactory, registryKey, redisLockProperties.getExpireAfter());
        log.info("Eca mail client redis lock registry [{}] has been initialized", registryKey);
        return redisLockRegistry;
    }
}
