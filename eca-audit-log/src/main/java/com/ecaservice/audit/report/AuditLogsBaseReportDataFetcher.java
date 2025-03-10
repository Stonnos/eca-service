package com.ecaservice.audit.report;

import com.ecaservice.audit.config.AuditLogProperties;
import com.ecaservice.audit.entity.AuditLogEntity;
import com.ecaservice.audit.mapping.AuditLogMapper;
import com.ecaservice.audit.report.model.AuditLogBean;
import com.ecaservice.audit.service.AuditLogService;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.report.data.fetcher.AbstractBaseReportDataFetcher;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.ecaservice.audit.dictionary.FilterDictionaries.AUDIT_LOG_TEMPLATE;

/**
 * Audit logs data fetcher for base report.
 *
 * @author Roman Batygin
 */
@Component
public class AuditLogsBaseReportDataFetcher
        extends AbstractBaseReportDataFetcher<AuditLogEntity, AuditLogBean> {

    private final AuditLogService auditLogService;
    private final AuditLogMapper auditLogMapper;

    /**
     * Constructor with spring dependency injection.
     *
     * @param auditLogProperties    - audit log properties
     * @param filterTemplateService - filter service bean
     * @param auditLogService       - audit log service bean
     * @param auditLogMapper        - audit log mapper bean
     */
    @Autowired
    public AuditLogsBaseReportDataFetcher(AuditLogProperties auditLogProperties,
                                          FilterTemplateService filterTemplateService,
                                          AuditLogService auditLogService,
                                          AuditLogMapper auditLogMapper) {
        super("AUDIT_LOGS", AUDIT_LOG_TEMPLATE, auditLogProperties.getMaxPagesNum(), filterTemplateService);
        this.auditLogService = auditLogService;
        this.auditLogMapper = auditLogMapper;
    }

    @Override
    protected Page<AuditLogEntity> getItemsPage(PageRequestDto pageRequestDto) {
        return auditLogService.getNextPage(pageRequestDto);
    }

    @Override
    protected List<AuditLogBean> convertToBeans(Page<AuditLogEntity> page) {
        return auditLogMapper.mapToBeans(page.getContent());
    }
}
