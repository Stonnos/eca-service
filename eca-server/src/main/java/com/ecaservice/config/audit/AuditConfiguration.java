package com.ecaservice.config.audit;

import com.ecaservice.core.audit.annotation.EnableAudit;
import com.ecaservice.core.audit.service.AuditEventInitiator;
import com.ecaservice.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Audit configuration class.
 *
 * @author Roman Batygin
 */
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
}
