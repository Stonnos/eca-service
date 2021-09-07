package com.ecaservice.controller.api;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.repository.ExperimentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

import static com.ecaservice.common.web.util.MaskUtils.mask;
import static com.ecaservice.util.ExperimentUtils.getExperimentFile;
import static com.ecaservice.util.Utils.buildAttachmentResponse;
import static com.ecaservice.util.Utils.existsFile;

/**
 * Implements REST API for ECA application.
 *
 * @author Roman Batygin
 */
@Tag(name = "API for ECA application")
@Slf4j
@RestController
@RequestMapping("/eca-api")
@RequiredArgsConstructor
public class EcaController {

    private final ExperimentRepository experimentRepository;

    /**
     * Downloads experiment results by token.
     *
     * @param token - experiment token
     */
    @Operation(
            description = "Downloads experiment results by token",
            summary = "Downloads experiment results by token"
    )
    @GetMapping(value = "/experiment/download/{token}")
    public ResponseEntity<FileSystemResource> downloadExperiment(
            @Parameter(description = "Experiment token", required = true)
            @PathVariable String token) {
        Experiment experiment = experimentRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException(Experiment.class, token));
        File experimentFile = getExperimentFile(experiment, Experiment::getExperimentAbsolutePath);
        if (!existsFile(experimentFile)) {
            log.error("Experiment results file not found for token [{}]", mask(token));
            return ResponseEntity.badRequest().build();
        }
        log.info("Downloads experiment file '{}' for token = '{}'", experiment.getExperimentAbsolutePath(),
                mask(token));
        return buildAttachmentResponse(experimentFile);
    }
}
