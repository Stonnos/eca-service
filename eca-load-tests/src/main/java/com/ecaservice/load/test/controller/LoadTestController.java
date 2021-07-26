package com.ecaservice.load.test.controller;

import com.ecaservice.load.test.dto.LoadTestDto;
import com.ecaservice.load.test.dto.LoadTestRequest;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.mapping.LoadTestMapper;
import com.ecaservice.load.test.report.bean.LoadTestBean;
import com.ecaservice.load.test.report.service.TestResultsReportDataFetcher;
import com.ecaservice.load.test.report.service.TestResultsReportGenerator;
import com.ecaservice.load.test.service.LoadTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Load tests controller.
 *
 * @author Roman Batygin
 */
@Tag(name = "API for load tests execution")
@Slf4j
@RestController
@RequestMapping("/load-tests")
@RequiredArgsConstructor
public class LoadTestController {

    private static final String ATTACHMENT_FORMAT = "attachment; filename=%s";
    private static final String LOAD_TEST_REPORT_NAME = "load-test-report%s.xlsx";

    private final LoadTestService loadTestService;
    private final TestResultsReportDataFetcher testResultsReportDataFetcher;
    private final TestResultsReportGenerator testResultsReportGenerator;
    private final LoadTestMapper loadTestMapper;

    /**
     * Creates load test.
     *
     * @param loadTestRequest - load test request
     * @return load test uuid
     */
    @Operation(
            description = "Creates load test",
            summary = "Creates load test"
    )
    @PostMapping(value = "/create")
    public LoadTestDto createTest(@Valid LoadTestRequest loadTestRequest) {
        log.info("Request for load test with params: {}", loadTestRequest);
        LoadTestEntity loadTestEntity = loadTestService.createTest(loadTestRequest);
        log.info("Load test has been created with uuid [{}]", loadTestEntity.getTestUuid());
        return loadTestMapper.mapToDto(loadTestEntity);
    }

    /**
     * Gets load test details.
     *
     * @param testUuid - test uuid
     */
    @Operation(
            description = "Gets load test details",
            summary = "Gets load test details"
    )
    @GetMapping(value = "/details/{testUuid}")
    public LoadTestDto getLoadTestDetails(@Parameter(description = "Test uuid", required = true)
                                          @PathVariable String testUuid) {
        log.info("Gets load test [{}] details", testUuid);
        LoadTestEntity loadTestEntity = loadTestService.getLoadTest(testUuid);
        return loadTestMapper.mapToDto(loadTestEntity);
    }

    /**
     * Downloads load test report in xlsx format.
     *
     * @param testUuid            - test uuid
     * @param httpServletResponse - http servlet response
     * @throws IOException in case of I/O error
     */
    @Operation(
            description = "Downloads load test report in xlsx format",
            summary = "Downloads load test report in xlsx format"
    )
    @GetMapping(value = "/report/{testUuid}")
    public void downloadReport(@Parameter(description = "Test uuid", required = true)
                               @PathVariable String testUuid,
                               HttpServletResponse httpServletResponse) throws IOException {
        log.info("Starting to download load test [{}] report", testUuid);
        LoadTestEntity loadTestEntity = loadTestService.getLoadTest(testUuid);
        @Cleanup OutputStream outputStream = httpServletResponse.getOutputStream();
        httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        String reportName = String.format(LOAD_TEST_REPORT_NAME, loadTestEntity.getTestUuid());
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(ATTACHMENT_FORMAT, reportName));
        LoadTestBean loadTestBean = testResultsReportDataFetcher.fetchReportData(loadTestEntity);
        log.info("Load test [{}] report data has been fetched", testUuid);
        testResultsReportGenerator.generateReport(loadTestBean, outputStream);
        outputStream.flush();
        log.info("Load test [{}] report has been generated", testUuid);
    }
}
