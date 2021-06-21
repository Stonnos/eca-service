package com.ecaservice.audit.report;

import com.ecaservice.audit.entity.AuditLogEntity;
import com.ecaservice.audit.mapping.AuditLogMapper;
import com.ecaservice.audit.service.AuditLogService;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.report.data.fetcher.AbstractBaseReportDataFetcher;
import com.ecaservice.report.model.AuditLogBean;
import com.ecaservice.report.model.ReportType;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

import static com.ecaservice.audit.dictionary.FilterDictionaries.AUDIT_LOG_TEMPLATE;

/**
 * Audit logs data fetcher for base report.
 *
 * @author Roman Batygin
 */
@Component
public class AuditLogsBaseReportDataFetcher extends AbstractBaseReportDataFetcher<AuditLogEntity, AuditLogBean> {

    private final AuditLogService auditLogService;
    private final AuditLogMapper auditLogMapper;

    /**
     * Constructor with spring dependency injection.
     *
     * @param filterService   - filter service bean
     * @param auditLogService - audit log service bean
     * @param auditLogMapper  - audit log mapper bean
     */
    @Inject
    public AuditLogsBaseReportDataFetcher(FilterService filterService,
                                          AuditLogService auditLogService,
                                          AuditLogMapper auditLogMapper) {
        super(ReportType.AUDIT_LOGS, AuditLogEntity.class, AUDIT_LOG_TEMPLATE, filterService);
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
