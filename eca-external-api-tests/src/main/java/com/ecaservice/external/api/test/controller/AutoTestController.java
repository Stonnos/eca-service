package com.ecaservice.external.api.test.controller;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.external.api.test.dto.AutoTestType;
import com.ecaservice.external.api.test.dto.AutoTestsJobDto;
import com.ecaservice.external.api.test.entity.JobEntity;
import com.ecaservice.external.api.test.report.ExternalApiTestResultsCsvReportGenerator;
import com.ecaservice.external.api.test.repository.JobRepository;
import com.ecaservice.external.api.test.service.JobService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Auto tests controller.
 *
 * @author Roman Batygin
 */
@Tag(name = "API for auto tests execution")
@Slf4j
@RestController
@RequestMapping("/auto-tests/external-api")
@RequiredArgsConstructor
public class AutoTestController {

    private static final String ATTACHMENT_FORMAT = "attachment; filename=%s";
    private static final String AUTO_TEST_REPORT_NAME = "auto-tests-report%s.zip";

    private final JobService jobService;
    private final ExternalApiTestResultsCsvReportGenerator externalApiTestResultsCsvReportGenerator;
    private final JobRepository jobRepository;

    /**
     * Creates auto tests job.
     *
     * @param autoTestType - auto test type
     * @return auto tests job dto
     */
    @Operation(
            description = "Creates auto tests job",
            summary = "Creates auto tests job"
    )
    @PostMapping(value = "/create-job")
    public AutoTestsJobDto createJob(@Parameter(description = "Auto test type", required = true)
                                     @RequestParam AutoTestType autoTestType) {
        log.info("Received auto tests request");
        var jobDto = jobService.createAndSaveNewJob(autoTestType);
        log.info("Auto tests job has been created with uuid [{}]", jobDto.getJobUuid());
        return jobDto;
    }

    /**
     * Gets auto tests job details.
     *
     * @param jobUuid - job uuid
     */
    @Operation(
            description = "Gets auto tests job details",
            summary = "Gets auto tests job details"
    )
    @GetMapping(value = "/details/{jobUuid}")
    public AutoTestsJobDto getDetails(
            @Parameter(description = "Job uuid", required = true) @PathVariable String jobUuid) {
        log.info("Request auto tests [{}] job details", jobUuid);
        return jobService.getDetails(jobUuid);
    }

    /**
     * Downloads auto tests report zip file.
     *
     * @param jobUuid             - job uuid
     * @param httpServletResponse - http servlet response
     * @throws IOException in case of I/O error
     */
    @Operation(
            description = "Downloads auto tests report zip file",
            summary = "Downloads auto tests report zip file"
    )
    @GetMapping(value = "/report/{jobUuid}")
    public void downloadReport(@Parameter(description = "Job uuid", required = true)
                               @PathVariable String jobUuid,
                               HttpServletResponse httpServletResponse) throws IOException {
        log.info("Starting to download auto tests [{}] report", jobUuid);
        JobEntity jobEntity = jobRepository.findByJobUuid(jobUuid)
                .orElseThrow(() -> new EntityNotFoundException(JobEntity.class, jobUuid));
        @Cleanup OutputStream outputStream = httpServletResponse.getOutputStream();
        httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        String reportName = String.format(AUTO_TEST_REPORT_NAME, jobEntity.getJobUuid());
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(ATTACHMENT_FORMAT, reportName));
        externalApiTestResultsCsvReportGenerator.generateReport(jobEntity, outputStream);
        outputStream.flush();
        log.info("Auto tests [{}] report has been generated", jobUuid);
    }
}
