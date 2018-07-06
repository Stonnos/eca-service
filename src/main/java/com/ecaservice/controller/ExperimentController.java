package com.ecaservice.controller;

import com.ecaservice.dto.EcaResponse;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.mapping.EcaResponseMapper;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.service.async.AsyncTaskService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.service.experiment.mail.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    private static final String ATTACHMENT = "attachment";

    private final ExperimentService experimentService;
    private final NotificationService notificationService;
    private final AsyncTaskService asyncTaskService;
    private final EcaResponseMapper ecaResponseMapper;

    /**
     * Constructor with dependency spring injection.
     *
     * @param experimentService   - experiment service bean
     * @param notificationService - notification service bean
     * @param asyncTaskService    - async task service bean
     * @param ecaResponseMapper   - eca response mapper bean
     */
    @Inject
    public ExperimentController(ExperimentService experimentService,
                                NotificationService notificationService,
                                AsyncTaskService asyncTaskService,
                                EcaResponseMapper ecaResponseMapper) {
        this.experimentService = experimentService;
        this.notificationService = notificationService;
        this.asyncTaskService = asyncTaskService;
        this.ecaResponseMapper = ecaResponseMapper;
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
        if (experimentFile == null) {
            log.error("Experiment results file for uuid = '{}' not found!", uuid);
            return ResponseEntity.badRequest().body(
                    String.format("Experiment results file for uuid = '%s' not found!", uuid));
        }
        FileSystemResource resource = new FileSystemResource(experimentFile);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(ATTACHMENT, resource.getFilename());
        log.info("Download file '{}' for uuid = '{}'", experimentFile.getAbsolutePath(), uuid);
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
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
        Experiment experiment = experimentService.createExperiment(experimentRequest);
        asyncTaskService.perform(() -> {
            try {
                notificationService.notifyByEmail(experiment);
            } catch (Exception ex) {
                log.error("There was an error while sending email request for experiment with id [{}]: {}",
                        experiment.getId(), ex.getMessage());
            }
        });
        EcaResponse ecaResponse = ecaResponseMapper.map(experiment);
        log.info("Experiment request has been created with status [{}].", ecaResponse.getStatus());
        return ResponseEntity.ok(ecaResponse);
    }

}
