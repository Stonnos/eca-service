package com.ecaservice.controller;

import com.ecaservice.dto.EcaResponse;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.service.experiment.ExperimentRequestService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.util.Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.File;

/**
 * Experiment controller.
 *
 * @author Roman Batygin
 */
@Api(tags = "Operations for experiment processing")
@Slf4j
@RestController
@RequestMapping("/experiment")
public class ExperimentController {

    private final ExperimentService experimentService;
    private final ExperimentRequestService experimentRequestService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param experimentService        - experiment service bean
     * @param experimentRequestService - experiment request service bean
     */
    @Inject
    public ExperimentController(ExperimentService experimentService,
                                ExperimentRequestService experimentRequestService) {
        this.experimentService = experimentService;
        this.experimentRequestService = experimentRequestService;
    }

    /**
     * Downloads experiment by specified uuid.
     *
     * @param uuid - experiment uuid
     */
    @ApiOperation(
            value = "Downloads experiment by specified uuid",
            notes = "Downloads experiment by specified uuid"
    )
    @GetMapping(value = "/download/{uuid}")
    public ResponseEntity downloadExperiment(@PathVariable String uuid) {
        File experimentFile = experimentService.findExperimentFileByUuid(uuid);
        if (experimentFile == null || !experimentFile.isFile()) {
            log.error("Experiment results file for uuid = '{}' not found!", uuid);
            return ResponseEntity.badRequest().body(
                    String.format("Experiment results file for uuid = '%s' not found!", uuid));
        }
        log.info("Download experiment file '{}' for uuid = '{}'", experimentFile.getAbsolutePath(), uuid);
        return Utils.buildAttachmentResponse(experimentFile);
    }

    /**
     * Creates experiment request.
     *
     * @param experimentRequest - experiment request dto
     * @return response entity
     */
    @ApiOperation(
            value = "Creates experiment request",
            notes = "Creates experiment request"
    )
    @PostMapping(value = "/create")
    public ResponseEntity<EcaResponse> createRequest(@RequestBody ExperimentRequest experimentRequest) {
        EcaResponse ecaResponse = experimentRequestService.createExperimentRequest(experimentRequest);
        return ResponseEntity.ok(ecaResponse);
    }

}
