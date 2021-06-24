package com.ecaservice.core.audit.service.template.impl;

import com.ecaservice.core.audit.AbstractJpaTest;
import com.ecaservice.core.audit.config.freemarker.AuditFreemarkerConfiguration;
import com.ecaservice.core.audit.config.freemarker.DatabaseTemplateLoader;
import com.ecaservice.core.audit.entity.AuditEventTemplateEntity;
import com.ecaservice.core.audit.mapping.AuditMapperImpl;
import com.ecaservice.core.audit.model.AuditContextParams;
import com.ecaservice.core.audit.repository.AuditCodeRepository;
import com.ecaservice.core.audit.repository.AuditEventTemplateRepository;
import com.ecaservice.core.audit.repository.AuditGroupRepository;
import com.ecaservice.core.audit.service.store.DatabaseAuditEventTemplateStore;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static com.ecaservice.core.audit.TestHelperUtils.createAuditEventTemplateEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link FreemarkerAuditTemplateProcessorService} class.
 *
 * @author Roman Batygin
 */
@Import({FreeMarkerAutoConfiguration.class, AuditFreemarkerConfiguration.class, DatabaseTemplateLoader.class,
        FreemarkerAuditTemplateProcessorService.class, DatabaseAuditEventTemplateStore.class, AuditMapperImpl.class})
class FreemarkerAuditTemplateProcessorServiceTest extends AbstractJpaTest {

    private static final String MESSAGE_TEMPLATE = "Audit ${param1}, ${param2} with result ${returnValue}";
    private static final String RESULT_VALUE = "ResultValue";
    private static final String PARAM_1 = "param1";
    private static final String VALUE_1 = "value1";
    private static final String PARAM_2 = "param2";
    private static final String VALUE_2 = "value2";
    private static final String EXPECTED_MESSAGE = "Audit value1, value2 with result ResultValue";

    @Inject
    private FreemarkerAuditTemplateProcessorService freemarkerAuditTemplateProcessorService;

    @Inject
    private AuditEventTemplateRepository auditEventTemplateRepository;
    @Inject
    private AuditCodeRepository auditCodeRepository;
    @Inject
    private AuditGroupRepository auditGroupRepository;

    private AuditEventTemplateEntity auditEventTemplateEntity;

    @Override
    public void init() {
        auditEventTemplateEntity = createAuditEventTemplateEntity();
        auditEventTemplateEntity.setMessageTemplate(MESSAGE_TEMPLATE);
        auditGroupRepository.save(auditEventTemplateEntity.getAuditCode().getAuditGroup());
        auditCodeRepository.save(auditEventTemplateEntity.getAuditCode());
        auditEventTemplateRepository.save(auditEventTemplateEntity);
    }

    @Override
    public void deleteAll() {
        auditEventTemplateRepository.deleteAll();
        auditCodeRepository.deleteAll();
        auditGroupRepository.deleteAll();
    }

    @Test
    void testProcessTemplate() {
        String auditCode = auditEventTemplateEntity.getAuditCode().getId();
        var contextParams = new AuditContextParams();
        contextParams.setInputParams(ImmutableMap.of(PARAM_1, VALUE_1, PARAM_2, VALUE_2));
        contextParams.setReturnValue(RESULT_VALUE);
        String message =
                freemarkerAuditTemplateProcessorService.process(auditCode, auditEventTemplateEntity.getEventType(),
                        contextParams);
        assertThat(message).isEqualTo(EXPECTED_MESSAGE);
    }
}
