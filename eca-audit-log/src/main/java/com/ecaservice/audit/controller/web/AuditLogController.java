package com.ecaservice.audit.controller.web;

import com.ecaservice.audit.entity.AuditLogEntity;
import com.ecaservice.audit.mapping.AuditLogMapper;
import com.ecaservice.audit.service.AuditLogService;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.web.dto.model.AuditLogDto;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import com.ecaservice.web.dto.model.FilterFieldDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static com.ecaservice.audit.dictionary.FilterDictionaries.AUDIT_GROUPS_DICTIONARY;
import static com.ecaservice.audit.dictionary.FilterDictionaries.AUDIT_LOG_TEMPLATE;

/**
 * Audit log API for web application.
 *
 * @author Roman Batygin
 */
@Validated
@Slf4j
@Api(tags = "Audit log API for web application")
@RestController
@RequestMapping("/audit-log")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final FilterService filterService;
    private final AuditLogMapper auditLogMapper;

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
    @GetMapping(value = "/filter-templates/audit-log")
    public List<FilterFieldDto> getAuditLogFilter() {
        return filterService.getFilterFields(AUDIT_LOG_TEMPLATE);
    }

    /**
     * Gets audit groups filter dictionary.
     *
     * @return filter fields list
     */
    @PreAuthorize("#oauth2.hasScope('web') and hasRole('ROLE_SUPER_ADMIN')")
    @ApiOperation(
            value = "Gets audit groups filter dictionary",
            notes = "Gets audit groups filter dictionary"
    )
    @GetMapping(value = "/filter-templates/groups")
    public FilterDictionaryDto getAuditGroupCodesDictionary() {
        return filterService.getFilterDictionary(AUDIT_GROUPS_DICTIONARY);
    }
}
