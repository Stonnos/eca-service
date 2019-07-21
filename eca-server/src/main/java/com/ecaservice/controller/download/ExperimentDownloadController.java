package com.ecaservice.controller.download;

import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.util.Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.File;

import static com.ecaservice.util.Utils.existsFile;

/**
 * Implements REST API for experiment results downloading.
 *
 * @author Roman Batygin
 */
@Api(tags = "API for experiment results downloading")
@Slf4j
@RestController
@RequestMapping("/experiment")
public class ExperimentDownloadController {

    private final ExperimentService experimentService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param experimentService - experiment service bean
     */
    @Inject
    public ExperimentDownloadController(ExperimentService experimentService) {
        this.experimentService = experimentService;
    }

    /**
     * Downloads experiment by specified uuid.
     *
     * @param uuid - experiment uuid
     */
    @ApiOperation(
            value = "Downloads experiment results by specified uuid",
            notes = "Downloads experiment results by specified uuid"
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
}
