package com.ecaservice.ers.controller.web;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.ers.report.EvaluationResultsHistoryReportDataFetcher;
import com.ecaservice.ers.service.EvaluationResultsHistoryService;
import com.ecaservice.ers.service.InstancesDataService;
import com.ecaservice.web.dto.model.EvaluationResultsHistoryDto;
import com.ecaservice.web.dto.model.EvaluationResultsHistoryPageDto;
import com.ecaservice.web.dto.model.FilterFieldDto;
import com.ecaservice.web.dto.model.InstancesInfoDto;
import com.ecaservice.web.dto.model.InstancesInfoPageDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;
import static com.ecaservice.ers.config.audit.AuditCodes.DOWNLOAD_EVALUATION_RESULTS_HISTORY_REPORT;
import static com.ecaservice.ers.dictionary.FilterDictionaries.EVALUATION_RESULTS_HISTORY_TEMPLATE;
import static com.ecaservice.ers.report.ReportTemplates.EVALUATION_RESULTS_HISTORY_TEMPLATE_CODE;
import static com.ecaservice.report.ReportGenerator.generateReport;

/**
 * Evaluation results API web application.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "Evaluation results history API for web application")
@RestController
@RequestMapping("/evaluation-results")
@RequiredArgsConstructor
public class EvaluationResultsHistoryController {

    private static final String FILE_NAME_FORMAT = "%s.xlsx";
    private static final String ATTACHMENT_FORMAT = "attachment; filename=%s";

    private final FilterTemplateService filterTemplateService;
    private final EvaluationResultsHistoryService evaluationResultsHistoryService;
    private final InstancesDataService instancesDataService;
    private final EvaluationResultsHistoryReportDataFetcher evaluationResultsHistoryReportDataFetcher;

    /**
     * Finds evaluation results history page with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return audit logs page
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Finds evaluation results history page with specified options such as filter, sorting and paging",
            summary = "Finds evaluation results history page with specified options such as filter, sorting and paging",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "EvaluationResultsHistoryPageRequest",
                                    ref = "#/components/examples/EvaluationResultsHistoryPageRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "EvaluationResultsHistoryPageResponse",
                                                    ref = "#/components/examples/EvaluationResultsHistoryPageResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = EvaluationResultsHistoryPageDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "BadPageRequestResponse",
                                                    ref = "#/components/examples/BadPageRequestResponse"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/history")
    public PageDto<EvaluationResultsHistoryDto> getEvaluationResultsHistoryPage(
            @Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received evaluation results history page request: {}", pageRequestDto);
        return evaluationResultsHistoryService.getNextPage(pageRequestDto);
    }

    /**
     * Gets evaluation results history filter fields.
     *
     * @return filter fields list
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Gets evaluation results history filter fields",
            summary = "Gets evaluation results history filter fields",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "EvaluationResultsHistoryFilterFieldsResponse",
                                                    ref = "#/components/examples/EvaluationResultsHistoryFilterFieldsResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "DataNotFoundResponse",
                                                    ref = "#/components/examples/DataNotFoundResponse"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @GetMapping(value = "/filter-templates/fields")
    public List<FilterFieldDto> getEvaluationResultsHistoryFilter() {
        return filterTemplateService.getFilterFields(EVALUATION_RESULTS_HISTORY_TEMPLATE);
    }

    /**
     * Finds instances info page with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return instances info page
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Finds instances info page with specified options such as filter, sorting and paging",
            summary = "Finds instances info page with specified options such as filter, sorting and paging",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "InstancesInfoPageRequest",
                                    ref = "#/components/examples/InstancesInfoPageRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "InstancesInfoPageResponse",
                                                    ref = "#/components/examples/InstancesInfoPageResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = InstancesInfoPageDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "BadPageRequestResponse",
                                                    ref = "#/components/examples/BadPageRequestResponse"
                                            )
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/instances/list")
    public PageDto<InstancesInfoDto> getInstancesInfoPage(@Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received instances info history page request: {}", pageRequestDto);
        return instancesDataService.getNextPage(pageRequestDto);
    }

    /**
     * Downloads evaluation results history base report in xlsx format.
     *
     * @param pageRequestDto      - page request dto
     * @param httpServletResponse - http servlet response
     * @throws IOException in case of I/O error
     */
    @Audit(value = DOWNLOAD_EVALUATION_RESULTS_HISTORY_REPORT)
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Downloads evaluation results history report in xlsx format",
            summary = "Downloads evaluation results history report in xlsx format",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "EvaluationResultsHistoryPageRequest",
                                    ref = "#/components/examples/EvaluationResultsHistoryPageRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "BadPageRequestResponse",
                                                    ref = "#/components/examples/BadPageRequestResponse"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/report/download")
    public void downloadEvaluationResultsHistoryReport(@Valid @RequestBody PageRequestDto pageRequestDto,
                                                       HttpServletResponse httpServletResponse)
            throws IOException {
        log.info("Request to download evaluation results history base report with params: {}", pageRequestDto);
        var baseReportBean = evaluationResultsHistoryReportDataFetcher.fetchReportData(pageRequestDto);
        String targetFile = String.format(FILE_NAME_FORMAT, EVALUATION_RESULTS_HISTORY_TEMPLATE_CODE);
        @Cleanup OutputStream outputStream = httpServletResponse.getOutputStream();
        httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(ATTACHMENT_FORMAT, targetFile));
        generateReport(EVALUATION_RESULTS_HISTORY_TEMPLATE_CODE, baseReportBean, outputStream);
        outputStream.flush();
    }
}
