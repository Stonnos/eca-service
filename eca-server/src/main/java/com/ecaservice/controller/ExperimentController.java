package com.ecaservice.controller;

import com.ecaservice.dto.EcaResponse;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.model.MultipartFileResource;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.service.experiment.ExperimentRequestService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.util.Utils;
import eca.core.evaluation.EvaluationMethod;
import eca.data.file.FileDataLoader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.File;

import static com.ecaservice.util.Utils.existsFile;

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
    public ResponseEntity downloadExperiment(
            @ApiParam(value = "Experiment uuid", required = true) @PathVariable String uuid) {
        File experimentFile = experimentService.findExperimentFileByUuid(uuid);
        if (!existsFile(experimentFile)) {
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

    /**
     * Creates experiment request.
     *
     * @param trainingData     - training data file with format, such as csv, xls, xlsx, arff, json, docx, data, txt
     * @param experimentType   - experiment type
     * @param evaluationMethod - evaluation method
     * @return response entity
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Creates experiment request with specified options",
            notes = "Creates experiment request with specified options"
    )
    @PostMapping(value = "/create")
    public ResponseEntity createRequest(
            @ApiParam(value = "Training data file", required = true) @RequestParam MultipartFile trainingData,
            @ApiParam(value = "Experiment type", required = true) @RequestParam ExperimentType experimentType,
            @ApiParam(value = "Evaluation method", required = true) @RequestParam EvaluationMethod evaluationMethod)
            throws Exception {
        log.info("Received experiment request for data '{}'", trainingData.getOriginalFilename());
        ExperimentRequest experimentRequest = new ExperimentRequest();
        experimentRequest.setFirstName("Роман");
        experimentRequest.setEmail("roman.batygin@mail.ru");
        FileDataLoader fileDataLoader = new FileDataLoader();
        fileDataLoader.setSource(new MultipartFileResource(trainingData));
        experimentRequest.setData(fileDataLoader.loadInstances());
        experimentRequest.setExperimentType(experimentType);
        experimentRequest.setEvaluationMethod(evaluationMethod);
        EcaResponse ecaResponse = experimentRequestService.createExperimentRequest(experimentRequest);
        return ResponseEntity.ok(ecaResponse);
    }

}
