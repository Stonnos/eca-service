package com.ecaservice.auto.test.controller;

import com.ecaservice.auto.test.service.AutoTestJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private final AutoTestJobService autoTestJobService;

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
}
