package com.ecaservice.controller.web;

import com.ecaservice.report.AbstractBaseReportDataFetcher;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.report.model.ReportType;
import com.ecaservice.web.dto.model.PageRequestDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static com.ecaservice.report.BaseReportGenerator.generateReport;

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
    private static final String FILE_NAME_FORMAT = "%s.xlsx";

    private final List<AbstractBaseReportDataFetcher> reportDataFetchers;

    /**
     * Downloads specified base report in xlsx format.
     *
     * @param pageRequestDto      - page request dto
     * @param httpServletResponse - http servlet response
     * @throws IOException in case of I/O error
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Downloads specified base report in xlsx format",
            notes = "Downloads specified base report in xlsx format"
    )
    @GetMapping(value = "/download")
    public void downloadReport(@Valid PageRequestDto pageRequestDto,
                               @ApiParam(value = "Report type", required = true) @RequestParam ReportType reportType,
                               HttpServletResponse httpServletResponse)
            throws IOException {
        AbstractBaseReportDataFetcher reportDataFetcher = getReportDataFetcher(reportType);
        BaseReportBean<?> baseReportBean = reportDataFetcher.fetchReportData(pageRequestDto);
        String fileName = String.format(FILE_NAME_FORMAT, reportType.getName());
        @Cleanup OutputStream outputStream = httpServletResponse.getOutputStream();
        httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(ATTACHMENT_FORMAT, fileName));
        generateReport(reportType, baseReportBean, outputStream);
        outputStream.flush();
    }

    private AbstractBaseReportDataFetcher getReportDataFetcher(ReportType reportType) {
        return reportDataFetchers.stream().filter(
                fetcher -> fetcher.getReportType().equals(reportType)).findFirst().orElseThrow(
                () -> new IllegalStateException(String.format("Can't handle [%s] report type", reportType)));
    }
}
