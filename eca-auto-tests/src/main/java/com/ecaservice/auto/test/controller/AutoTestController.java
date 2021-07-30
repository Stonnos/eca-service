package com.ecaservice.auto.test.controller;

import com.ecaservice.auto.test.report.AutoTestsScvReportGenerator;
import com.ecaservice.auto.test.service.AutoTestJobService;
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
import java.io.IOException;

/**
 * Auto tests controller.
 *
 * @author Roman Batygin
 */
@Tag(name = "API for auto tests execution")
@Slf4j
@RestController
@RequestMapping("/auto-tests")
@RequiredArgsConstructor
public class AutoTestController {

    private static final String ATTACHMENT_FORMAT = "attachment; filename=%s";
    private static final String AUTO_TEST_REPORT_NAME = "experiment-auto-tests-report%s.zip";

    private final AutoTestJobService autoTestJobService;
    private final AutoTestsScvReportGenerator autoTestsScvReportGenerator;

    /**
     * Creates experiment requests auto tests job.
     *
     * @return load test uuid
     */
    @Operation(
            description = "Creates experiment requests tests job",
            summary = "Creates experiment requests tests job"
    )
    @PostMapping(value = "/experiments/create")
    public String createExperimentsAutoTestsJob() {
        log.info("Request to create auto tests job for experiment requests");
        var autoTestsJobEntity = autoTestJobService.createExperimentsAutoTestsJob();
        log.info("Experiment requests auto test job has been created with uuid [{}]", autoTestsJobEntity.getJobUuid());
        return autoTestsJobEntity.getJobUuid();
    }

    /**
     * Downloads experiment auto tests report zip file.
     *
     * @param jobUuid             - job uuid
     * @param httpServletResponse - http servlet response
     * @throws IOException in case of I/O error
     */
    @Operation(
            description = "Downloads experiment auto tests report zip file",
            summary = "Downloads experiment auto tests report zip file"
    )
    @GetMapping(value = "/experiments/report/{jobUuid}")
    public void downloadReport(@Parameter(description = "Job uuid", required = true)
                               @PathVariable String jobUuid,
                               HttpServletResponse httpServletResponse) throws IOException {
        log.info("Starting to download experiment auto tests [{}] report", jobUuid);
        var jobEntity = autoTestJobService.getJob(jobUuid);
        @Cleanup var outputStream = httpServletResponse.getOutputStream();
        httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        String reportName = String.format(AUTO_TEST_REPORT_NAME, jobEntity.getJobUuid());
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(ATTACHMENT_FORMAT, reportName));
        autoTestsScvReportGenerator.generateReport(jobEntity, outputStream);
        outputStream.flush();
        log.info("Experiment auto tests [{}] report has been generated", jobUuid);
    }
}
