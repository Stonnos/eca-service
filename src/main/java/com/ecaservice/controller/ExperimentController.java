package com.ecaservice.controller;

import com.ecaservice.dto.EcaResponse;
import com.ecaservice.dto.ExperimentRequestDto;
import com.ecaservice.mapping.EcaResponseMapper;
import com.ecaservice.mapping.ExperimentRequestMapper;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentRequest;
import com.ecaservice.service.experiment.ExperimentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Experiment controller.
 *
 * @author Roman Batygin
 */
@Slf4j
@Controller
@RequestMapping("/eca-service/experiment")
public class ExperimentController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss");

    private final ExperimentService experimentService;
    private final ExperimentRequestMapper experimentRequestMapper;
    private final EcaResponseMapper ecaResponseMapper;

    /**
     * Constructor with dependency spring injection.
     * @param experimentService {@link ExperimentService} bean
     * @param experimentRequestMapper {@link ExperimentRequestMapper} bean
     * @param ecaResponseMapper {@link EcaResponseMapper} bean
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
     * Downloads experiment.
     * @param uuid experiment uuid
     * @param response {@link HttpServletResponse} object
     */
    @RequestMapping(value = "/download/{uuid}", method = RequestMethod.GET)
    public void downloadExperiment(@PathVariable String uuid, HttpServletResponse response) {
        File experimentFile = experimentService.findExperimentFileByUuid(uuid);
        if (Objects.isNull(experimentFile)) {
            log.warn("Experiment file by uuid {} not found!", uuid);
        } else {
            response.setContentType("text/plain");
            response.setContentLength((int) experimentFile.length());
            response.setHeader("Content-Disposition",
                    String.format("attachment; filename=%s", experimentFile.getName()));
            try (OutputStream outputStream = response.getOutputStream()) {
                FileUtils.copyFile(experimentFile, outputStream);
                outputStream.flush();
            } catch (Exception ex) {
                log.error("There was an error {} while downloading file {}", ex.getMessage(), experimentFile.getName());
            }
        }
    }

    /**
     * Creates experiment request.
     * @param experimentRequest {@link ExperimentRequestDto} object
     * @param httpServletRequest {@link HttpServletRequest} object
     * @return {@link ResponseEntity} object
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<EcaResponse> createRequest(@RequestBody ExperimentRequestDto experimentRequest,
                                                     HttpServletRequest httpServletRequest) {
        log.info("Received request to experiment {} for client {} at: {}", experimentRequest.getExperimentType(),
                httpServletRequest.getRemoteAddr(), DATE_FORMAT.format(LocalDateTime.now()));
        ExperimentRequest request = experimentRequestMapper.map(experimentRequest);
        request.setIpAddress(httpServletRequest.getRemoteAddr());
        Experiment experiment = experimentService.createExperiment(request);
        EcaResponse ecaResponse = ecaResponseMapper.map(experiment);
        log.info("Experiment request has been created with status [{}].", ecaResponse.getStatus());
        return new ResponseEntity(ecaResponse, HttpStatus.OK);
    }

}
