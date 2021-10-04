package com.ecaservice.server.config.audit;

import com.ecaservice.core.audit.annotation.EnableAudit;
import com.ecaservice.core.audit.service.AuditEventInitiator;
import com.ecaservice.core.lock.redis.config.RedisLockProperties;
import com.ecaservice.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

import static com.ecaservice.core.audit.config.AuditCoreConfiguration.AUDIT_LOCK_REGISTRY;

/**
 * Audit configuration class.
 *
 * @author Roman Batygin
 */
@Slf4j
@EnableAudit
@Configuration
public class AuditConfiguration {

    /**
     * Creates audit event initiator bean.
     *
     * @param userService - user service
     * @return audit event initiator bean
     */
    @Primary
    @Bean
    public AuditEventInitiator auditEventInitiator(UserService userService) {
        return userService::getCurrentUser;
    }

    /**
     * Creates redis lock registry for audit.
     *
     * @param redisConnectionFactory - redis connection factory
     * @return audit redis lock registry
     */
    @Bean(AUDIT_LOCK_REGISTRY)
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory,
                                               RedisLockProperties redisLockProperties) {
        String registryKey = String.format("%s-audit", redisLockProperties.getRegistryKey());
        var redisLockRegistry =
                new RedisLockRegistry(redisConnectionFactory, registryKey, redisLockProperties.getExpireAfter());
        log.info("Audit redis lock registry [{}] has been initialized", registryKey);
        return redisLockRegistry;
    }
}
