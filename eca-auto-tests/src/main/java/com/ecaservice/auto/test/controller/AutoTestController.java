package com.ecaservice.auto.test.controller;

import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.report.AbstractAutoTestsScvReportGenerator;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

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
    private static final String AUTO_TEST_REPORT_NAME = "auto-tests-report-%s.zip";

    private final AutoTestJobService autoTestJobService;
    private final List<AbstractAutoTestsScvReportGenerator> autoTestsScvReportGenerators;

    /**
     * Creates auto tests job.
     *
     * @param autoTestType - auto test type
     * @return load test uuid
     */
    @Operation(
            description = "Creates auto tests job",
            summary = "Creates auto tests job"
    )
    @PostMapping(value = "/create")
    public String createAutoTestsJob(@Parameter(description = "Auto test type", required = true)
                                     @RequestParam AutoTestType autoTestType) {
        log.info("Request to create auto tests [{}] job", autoTestType);
        var autoTestsJobEntity = autoTestJobService.createExperimentsAutoTestsJob(autoTestType);
        log.info("Auto test [{}] job has been created with uuid [{}]", autoTestType, autoTestsJobEntity.getJobUuid());
        return autoTestsJobEntity.getJobUuid();
    }

    /**
     * Downloads auto tests report zip file.
     *
     * @param jobUuid             - job uuid
     * @param httpServletResponse - http servlet response
     * @throws IOException in case of I/O error
     */
    @SuppressWarnings("unchecked")
    @Operation(
            description = "Downloads auto tests report zip file",
            summary = "Downloads auto tests report zip file"
    )
    @GetMapping(value = "/report/{jobUuid}")
    public void downloadReport(@Parameter(description = "Job uuid", required = true)
                               @PathVariable String jobUuid,
                               HttpServletResponse httpServletResponse) throws IOException {
        log.info("Starting to download auto tests [{}] report", jobUuid);
        var jobEntity = autoTestJobService.getJob(jobUuid);
        var reportGenerator = getReportGenerator(jobEntity);
        @Cleanup var outputStream = httpServletResponse.getOutputStream();
        httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        String reportName = String.format(AUTO_TEST_REPORT_NAME, jobEntity.getJobUuid());
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(ATTACHMENT_FORMAT, reportName));
        reportGenerator.generateReport(jobEntity, outputStream);
        outputStream.flush();
        log.info("Auto tests [{}] report has been generated", jobUuid);
    }

    private AbstractAutoTestsScvReportGenerator getReportGenerator(AutoTestsJobEntity autoTestsJobEntity) {
        return autoTestsScvReportGenerators.stream()
                .filter(generator -> generator.canHandle(autoTestsJobEntity))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Unexpected auto test type [%s]",
                        autoTestsJobEntity.getAutoTestType())));
    }
}
