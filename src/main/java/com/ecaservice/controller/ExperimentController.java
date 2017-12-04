package com.ecaservice.controller;

import com.ecaservice.dto.EcaResponse;
import com.ecaservice.dto.ExperimentRequestDto;
import com.ecaservice.mapping.EcaResponseMapper;
import com.ecaservice.mapping.ExperimentRequestMapper;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentRequest;
import com.ecaservice.service.experiment.ExperimentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Experiment controller.
 *
 * @author Roman Batygin
 */
@Slf4j
@RestController
@RequestMapping("/eca-service/experiment")
public class ExperimentController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss");
    private static final String ATTACHMENT = "attachment";

    private final ExperimentService experimentService;
    private final ExperimentRequestMapper experimentRequestMapper;
    private final EcaResponseMapper ecaResponseMapper;

    /**
     * Constructor with dependency spring injection.
     *
     * @param experimentService       {@link ExperimentService} bean
     * @param experimentRequestMapper {@link ExperimentRequestMapper} bean
     * @param ecaResponseMapper       {@link EcaResponseMapper} bean
     */
    @Autowired
    public ExperimentController(ExperimentService experimentService,
                                ExperimentRequestMapper experimentRequestMapper,
                                EcaResponseMapper ecaResponseMapper) {
        this.experimentService = experimentService;
        this.experimentRequestMapper = experimentRequestMapper;
        this.ecaResponseMapper = ecaResponseMapper;
    }

    /**
     * Downloads experiment by specified uuid.
     *
     * @param uuid experiment uuid
     */
    @RequestMapping(value = "/download/{uuid}", method = RequestMethod.GET)
    public ResponseEntity downloadExperiment(@PathVariable String uuid) {
        File experimentFile = experimentService.findExperimentFileByUuid(uuid);
        if (experimentFile == null) {
            log.error("Experiment results file for uuid = '{}' not found!", uuid);
            return new ResponseEntity<>(String.format("Experiment results file for uuid = '%s' not found!", uuid),
                    HttpStatus.BAD_REQUEST);
        }
        FileSystemResource resource = new FileSystemResource(experimentFile);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(ATTACHMENT, resource.getFilename());
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    /**
     * Creates experiment request.
     *
     * @param experimentRequest  {@link ExperimentRequestDto} object
     * @param httpServletRequest {@link HttpServletRequest} object
     * @return {@link ResponseEntity} object
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<EcaResponse> createRequest(@RequestBody ExperimentRequestDto experimentRequest,
                                                     HttpServletRequest httpServletRequest) {
        log.info("Received request to experiment {} for client {} at: {}", experimentRequest.getExperimentType(),
                httpServletRequest.getRemoteAddr(), DATE_FORMAT.format(LocalDateTime.now()));
        ExperimentRequest request = experimentRequestMapper.map(experimentRequest);
        request.setIpAddress(httpServletRequest.getRemoteAddr());
        Experiment experiment = experimentService.createExperiment(request);
        EcaResponse ecaResponse = ecaResponseMapper.map(experiment);
        log.info("Experiment request has been created with status [{}].", ecaResponse.getStatus());
        return new ResponseEntity<>(ecaResponse, HttpStatus.OK);
    }

}
