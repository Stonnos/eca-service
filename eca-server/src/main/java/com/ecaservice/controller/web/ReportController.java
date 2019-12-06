package com.ecaservice.controller.web;

import com.ecaservice.report.EvaluationLogsBaseReportDataFetcher;
import com.ecaservice.report.ExperimentsBaseReportDataFetcher;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.report.model.EvaluationLogBean;
import com.ecaservice.report.model.ExperimentBean;
import com.ecaservice.web.dto.model.PageRequestDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static com.ecaservice.report.BaseReportGenerator.generateEvaluationLogsReport;
import static com.ecaservice.report.BaseReportGenerator.generateExperimentsReport;

/**
 * Controller for reports downloading.
 *
 * @author Roman Batygin
 */
@Api(tags = "Reports controller for web application")
@Slf4j
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private static final String ATTACHMENT_FORMAT = "attachment; filename=%s";
    private static final String EXPERIMENTS_REPORT_NAME = "experiments-report.xlsx";
    private static final String EVALUATION_LOGS_REPORT_NAME = "evaluation-logs-report.xlsx";

    private final ExperimentsBaseReportDataFetcher experimentsBaseReportDataFetcher;
    private final EvaluationLogsBaseReportDataFetcher evaluationLogsBaseReportDataFetcher;

    /**
     * Downloads experiments base report in xlsx format.
     *
     * @param pageRequestDto      - page request dto
     * @param httpServletResponse - http servlet response
     * @throws IOException in case of I/O error
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Downloads experiments base report in xlsx format",
            notes = "Downloads experiments base report in xlsx format"
    )
    @GetMapping(value = "/experiments")
    public void downloadExperimentsReport(PageRequestDto pageRequestDto,
                                        HttpServletResponse httpServletResponse) throws IOException {
        @Cleanup OutputStream outputStream = httpServletResponse.getOutputStream();
        httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                String.format(ATTACHMENT_FORMAT, EXPERIMENTS_REPORT_NAME));
        BaseReportBean<ExperimentBean> baseReportBean =
                experimentsBaseReportDataFetcher.fetchReportData(pageRequestDto);
        generateExperimentsReport(baseReportBean, outputStream);
        outputStream.flush();
    }

    /**
     * Downloads evaluation logs base report in xlsx format.
     *
     * @param pageRequestDto      - page request dto
     * @param httpServletResponse - http servlet response
     * @throws IOException in case of I/O error
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Downloads evaluation logs base report in xlsx format",
            notes = "Downloads evaluation logs base report in xlsx format"
    )
    @GetMapping(value = "/evaluations")
    public void downloadEvaluationLogs(PageRequestDto pageRequestDto,
                                     HttpServletResponse httpServletResponse) throws IOException {
        @Cleanup OutputStream outputStream = httpServletResponse.getOutputStream();
        httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                String.format(ATTACHMENT_FORMAT, EVALUATION_LOGS_REPORT_NAME));
        BaseReportBean<EvaluationLogBean> baseReportBean =
                evaluationLogsBaseReportDataFetcher.fetchReportData(pageRequestDto);
        generateEvaluationLogsReport(baseReportBean, outputStream);
        outputStream.flush();
    }
}
