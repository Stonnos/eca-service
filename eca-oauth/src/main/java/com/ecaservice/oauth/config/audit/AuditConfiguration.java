package com.ecaservice.oauth.config.audit;

import com.ecaservice.core.audit.annotation.EnableAudit;
import com.ecaservice.core.audit.service.AuditEventInitiator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.context.SecurityContextHolder;

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
     * @return audit event initiator bean
     */
    @Primary
    @Bean
    public AuditEventInitiator auditEventInitiator() {
        return () -> SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
