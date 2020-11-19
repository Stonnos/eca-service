package com.ecaservice.external.api.test.controller;

import com.ecaservice.external.api.test.entity.JobEntity;
import com.ecaservice.external.api.test.repository.JobRepository;
import com.ecaservice.external.api.test.service.JobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Auto tests controller.
 *
 * @author Roman Batygin
 */
@Api(tags = "API for auto tests execution")
@Slf4j
@RestController
@RequestMapping("/auto-tests")
@RequiredArgsConstructor
public class AutoTestController {

    private static final String ATTACHMENT_FORMAT = "attachment; filename=%s";
    private static final String LOAD_TEST_REPORT_NAME = "auto-tests-report%s.zip";

    private final JobService jobService;
    private final JobRepository jobRepository;

    /**
     * Creates auto tests job.
     *
     * @return job uuid
     */
    @ApiOperation(
            value = "Creates auto tests job",
            notes = "Creates auto tests job"
    )
    @PostMapping(value = "/create-job")
    public String createJob() {
        log.info("Received auto tests request");
        JobEntity jobEntity = jobService.createAndSaveNewJob();
        log.info("Auto tests job has been created with uuid [{}]", jobEntity.getJobUuid());
        return jobEntity.getJobUuid();
    }

    /**
     * Downloads auto tests report zip file.
     *
     * @param jobUuid             - job uuid
     * @param httpServletResponse - http servlet response
     * @throws IOException in case of I/O error
     */
    @ApiOperation(
            value = "Downloads auto tests report zip file",
            notes = "Downloads auto tests report zip file"
    )
    @GetMapping(value = "/report/{jobUuid}")
    public void downloadReport(@ApiParam(value = "Test uuid", required = true) @PathVariable String jobUuid,
                               HttpServletResponse httpServletResponse) throws IOException {
        log.info("Starting to download auto tests [{}] report", jobUuid);
       /* JobEntity loadTestEntity = loadTestService.getLoadTest(jobUuid);
        @Cleanup OutputStream outputStream = httpServletResponse.getOutputStream();
        httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        String reportName = String.format(LOAD_TEST_REPORT_NAME, loadTestEntity.getjobUuid());
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(ATTACHMENT_FORMAT, reportName));
        LoadTestBean loadTestBean = testResultsReportDataFetcher.fetchReportData(loadTestEntity);
        log.info("Load test [{}] report data has been fetched", jobUuid);
        testResultsReportGenerator.generateReport(loadTestBean, outputStream);
        outputStream.flush();*/
        log.info("Auto tests [{}] report has been generated", jobUuid);
    }
}
