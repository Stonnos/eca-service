package com.ecaservice.server.controller.web;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.report.data.fetcher.AbstractBaseReportDataFetcher;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.server.report.model.BaseReportType;
import com.ecaservice.web.dto.model.PageRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;
import static com.ecaservice.server.config.audit.AuditCodes.GENERATE_EVALUATION_REQUESTS_REPORT;
import static com.ecaservice.server.report.ReportTemplates.BASE_REPORT_TEMPLATES;
import static com.ecaservice.server.util.ReportHelper.download;

/**
 * Controller for reports downloading.
 *
 * @author Roman Batygin
 */
@Tag(name = "Reports controller for web application")
@Slf4j
@Validated
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final List<AbstractBaseReportDataFetcher> reportDataFetchers;

    /**
     * Downloads specified base report in xlsx format.
     *
     * @param pageRequestDto      - page request dto
     * @param httpServletResponse - http servlet response
     * @throws IOException in case of I/O error
     */
    @Audit(value = GENERATE_EVALUATION_REQUESTS_REPORT)
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Downloads specified base report in xlsx format",
            summary = "Downloads specified base report in xlsx format",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "PageRequest",
                                    ref = "#/components/examples/PageRequest"
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
    @PostMapping(value = "/download")
    public void downloadReport(@Valid @RequestBody PageRequestDto pageRequestDto,
                               @Parameter(description = "Report type", required = true)
                               @RequestParam BaseReportType reportType,
                               HttpServletResponse httpServletResponse)
            throws IOException {
        log.info("Request to download base report [{}] with params: {}", reportType, pageRequestDto);
        AbstractBaseReportDataFetcher reportDataFetcher = getReportDataFetcher(reportType);
        BaseReportBean<?> baseReportBean = reportDataFetcher.fetchReportData(pageRequestDto);
        String template = BASE_REPORT_TEMPLATES.get(reportType);
        download(template, template, httpServletResponse, baseReportBean);
    }

    private AbstractBaseReportDataFetcher getReportDataFetcher(BaseReportType reportType) {
        return reportDataFetchers.stream()
                .filter(fetcher -> fetcher.getReportType().equals(reportType.name()))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException(String.format("Can't handle [%s] report type", reportType)));
    }
}
