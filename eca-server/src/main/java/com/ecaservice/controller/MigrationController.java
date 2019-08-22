package com.ecaservice.controller;

import com.ecaservice.service.experiment.ExperimentResultsMigrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * @author Roman Batygin
 */
@RestController
public class MigrationController {

    private final ExperimentResultsMigrationService experimentResultsMigrationService;

    @Inject
    public MigrationController(
            ExperimentResultsMigrationService experimentResultsMigrationService) {
        this.experimentResultsMigrationService = experimentResultsMigrationService;
    }

    @GetMapping("/migrate")
    public ResponseEntity migrate() {
        experimentResultsMigrationService.migrate();
        return ResponseEntity.ok().build();
    }
}
