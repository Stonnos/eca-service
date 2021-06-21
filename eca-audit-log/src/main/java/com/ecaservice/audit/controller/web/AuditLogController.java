package com.ecaservice.audit.controller.web;

import com.ecaservice.audit.entity.AuditLogEntity;
import com.ecaservice.audit.mapping.AuditLogMapper;
import com.ecaservice.audit.report.AuditLogsBaseReportDataFetcher;
import com.ecaservice.audit.service.AuditLogService;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.report.model.ReportType;
import com.ecaservice.web.dto.model.AuditLogDto;
import com.ecaservice.web.dto.model.FilterFieldDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static com.ecaservice.audit.dictionary.FilterDictionaries.AUDIT_LOG_TEMPLATE;
import static com.ecaservice.report.ReportGenerator.generateReport;

/**
 * Audit log API for web application.
 *
 * @author Roman Batygin
 */
@Slf4j
@Api(tags = "Audit log API for web application")
@RestController
@RequestMapping("/audit-log")
@RequiredArgsConstructor
public class AuditLogController {

    private static final String FILE_NAME_FORMAT = "%s.xlsx";
    private static final String ATTACHMENT_FORMAT = "attachment; filename=%s";

    private final AuditLogService auditLogService;
    private final FilterService filterService;
    private final AuditLogMapper auditLogMapper;
    private final AuditLogsBaseReportDataFetcher auditLogsBaseReportDataFetcher;

    /**
     * Finds audit logs with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return audit logs page
     */
    @PreAuthorize("#oauth2.hasScope('web') and hasRole('ROLE_SUPER_ADMIN')")
    @ApiOperation(
            value = "Finds audit logs with specified options such as filter, sorting and paging",
            notes = "Finds audit logs with specified options such as filter, sorting and paging"
    )
    @GetMapping(value = "/list")
    public PageDto<AuditLogDto> getAuditLogsPage(@Valid PageRequestDto pageRequestDto) {
        log.info("Received audit logs page request: {}", pageRequestDto);
        Page<AuditLogEntity> auditLogsPage = auditLogService.getNextPage(pageRequestDto);
        List<AuditLogDto> auditLogDtoList = auditLogMapper.map(auditLogsPage.getContent());
        return PageDto.of(auditLogDtoList, pageRequestDto.getPage(), auditLogsPage.getTotalElements());
    }

    /**
     * Gets audit log filter fields.
     *
     * @return filter fields list
     */
    @PreAuthorize("#oauth2.hasScope('web') and hasRole('ROLE_SUPER_ADMIN')")
    @ApiOperation(
            value = "Gets audit log filter fields",
            notes = "Gets audit log filter fields"
    )
    @GetMapping(value = "/filter-templates/fields")
    public List<FilterFieldDto> getAuditLogFilter() {
        return filterService.getFilterFields(AUDIT_LOG_TEMPLATE);
    }

    /**
     * Downloads audit logs base report in xlsx format.
     *
     * @param pageRequestDto      - page request dto
     * @param httpServletResponse - http servlet response
     * @throws IOException in case of I/O error
     */
    @PreAuthorize("#oauth2.hasScope('web') and hasRole('ROLE_SUPER_ADMIN')")
    @ApiOperation(
            value = "Downloads audit logs base report in xlsx format",
            notes = "Downloads audit logs base report in xlsx format"
    )
    @GetMapping(value = "/report/download")
    public void downloadReport(@Valid PageRequestDto pageRequestDto, HttpServletResponse httpServletResponse)
            throws IOException {
        log.info("Request to download audit logs base report with params: {}", pageRequestDto);
        var baseReportBean = auditLogsBaseReportDataFetcher.fetchReportData(pageRequestDto);
        String targetFile = String.format(FILE_NAME_FORMAT, ReportType.AUDIT_LOGS.getName());
        @Cleanup OutputStream outputStream = httpServletResponse.getOutputStream();
        httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(ATTACHMENT_FORMAT, targetFile));
        generateReport(ReportType.AUDIT_LOGS, baseReportBean, outputStream);
        outputStream.flush();
    }
}
