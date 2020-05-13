package com.ecaservice.controller.api;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.experiment.mail.EmailClient;
import com.ecaservice.service.experiment.mail.EmailRequest;
import com.ecaservice.service.experiment.mail.EmailResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

import static com.ecaservice.util.ExperimentUtils.getExperimentFile;
import static com.ecaservice.util.Utils.buildAttachmentResponse;
import static com.ecaservice.util.Utils.existsFile;

/**
 * Implements REST API for ECA application.
 *
 * @author Roman Batygin
 */
@Api(tags = "API for ECA application")
@Slf4j
@RestController
@RequestMapping("/eca-api")
@RequiredArgsConstructor
public class EcaController {

    private final ExperimentRepository experimentRepository;
    private final EmailClient emailClient;

    @GetMapping(value = "/send")
    public EmailResponse send() {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setHtml(false);
        emailRequest.setMessage("Hello!");
        emailRequest.setReceiver("roman.batygin@mail.ru");
        emailRequest.setSender("rbatsw@gmail.com");
        return emailClient.sendEmail(emailRequest);
    }

    /**
     * Downloads experiment results by token.
     *
     * @param token - experiment token
     */
    @ApiOperation(
            value = "Downloads experiment results by token",
            notes = "Downloads experiment results by token"
    )
    @GetMapping(value = "/experiment/download/{token}")
    public ResponseEntity<FileSystemResource> downloadExperiment(
            @ApiParam(value = "Experiment token", required = true) @PathVariable String token) {
        Experiment experiment = experimentRepository.findByToken(token);
        if (experiment == null) {
            log.error("Experiment with token [{}] not found", token);
            return ResponseEntity.notFound().build();
        }
        File experimentFile = getExperimentFile(experiment, Experiment::getExperimentAbsolutePath);
        if (!existsFile(experimentFile)) {
            log.error("Experiment results file not found for token [{}]", token);
            return ResponseEntity.notFound().build();
        }
        log.info("Downloads experiment file '{}' for token = '{}'", experiment.getExperimentAbsolutePath(), token);
        return buildAttachmentResponse(experimentFile);
    }
}
