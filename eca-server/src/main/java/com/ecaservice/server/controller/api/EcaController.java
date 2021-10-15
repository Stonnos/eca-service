package com.ecaservice.server.controller.api;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.repository.ExperimentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

import static com.ecaservice.common.web.util.MaskUtils.mask;
import static com.ecaservice.server.util.ExperimentUtils.getExperimentFile;
import static com.ecaservice.server.util.Utils.buildAttachmentResponse;
import static com.ecaservice.server.util.Utils.existsFile;
import static com.ecaservice.web.dto.doc.CommonApiExamples.DATA_NOT_FOUND_RESPONSE_JSON;

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
            summary = "Downloads experiment results by token",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = DATA_NOT_FOUND_RESPONSE_JSON),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
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
