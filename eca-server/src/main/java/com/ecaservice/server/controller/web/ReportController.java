package com.ecaservice.server.controller.web;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.report.data.fetcher.AbstractBaseReportDataFetcher;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.report.model.ReportType;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;
import static com.ecaservice.server.util.ReportHelper.download;
import static com.ecaservice.web.dto.doc.CommonApiExamples.INVALID_PAGE_REQUEST_RESPONSE_JSON;
import static com.ecaservice.web.dto.doc.CommonApiExamples.SIMPLE_PAGE_REQUEST_JSON;
import static com.ecaservice.web.dto.doc.CommonApiExamples.UNAUTHORIZED_RESPONSE_JSON;

/**
 * Controller for reports downloading.
 *
 * @author Roman Batygin
 */
@Tag(name = "Reports controller for web application")
@Slf4j
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
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Downloads specified base report in xlsx format",
            summary = "Downloads specified base report in xlsx format",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = SIMPLE_PAGE_REQUEST_JSON)
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = INVALID_PAGE_REQUEST_RESPONSE_JSON),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/download")
    public void downloadReport(@Valid @RequestBody PageRequestDto pageRequestDto,
                               @Parameter(description = "Report type", required = true)
                               @RequestParam ReportType reportType,
                               HttpServletResponse httpServletResponse)
            throws IOException {
        log.info("Request to download base report [{}] with params: {}", reportType, pageRequestDto);
        AbstractBaseReportDataFetcher reportDataFetcher = getReportDataFetcher(reportType);
        BaseReportBean<?> baseReportBean = reportDataFetcher.fetchReportData(pageRequestDto);
        download(reportType, reportType.getName(), httpServletResponse, baseReportBean);
    }

    private AbstractBaseReportDataFetcher getReportDataFetcher(ReportType reportType) {
        return reportDataFetchers.stream()
                .filter(fetcher -> fetcher.getReportType().equals(reportType))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException(String.format("Can't handle [%s] report type", reportType)));
    }
}
