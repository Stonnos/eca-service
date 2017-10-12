package com.ecaservice.controller;

import com.ecaservice.dto.EcaResponse;
import com.ecaservice.dto.ExperimentRequestDto;
import com.ecaservice.mapping.OrikaBeanMapper;
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

/**
 * Experiment controller.
 *
 * @author Roman Batygin
 */
@Slf4j
@Controller
@RequestMapping("/eca-service/experiment")
public class ExperimentController {

    private final ExperimentService experimentService;
    private final OrikaBeanMapper mapper;

    /**
     * Constructor with dependency spring injection.
     *
     * @param experimentService {@link ExperimentService} bean
     * @param mapper            {@link OrikaBeanMapper} bean
     */
    @Autowired
    public ExperimentController(ExperimentService experimentService, OrikaBeanMapper mapper) {
        this.experimentService = experimentService;
        this.mapper = mapper;
    }

    @RequestMapping(value = "/download/{uuid}", method = RequestMethod.GET)
    public void downloadExperiment(@PathVariable String uuid, HttpServletResponse response) {
        File experimentFile = experimentService.findExperimentFileByUuid(uuid);
        if (experimentFile == null) {
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

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<EcaResponse> createRequest(@RequestBody ExperimentRequestDto experimentRequest,
                                                     HttpServletRequest httpServletRequest) {
        ExperimentRequest request = mapper.map(experimentRequest, ExperimentRequest.class);
        request.setIpAddress(httpServletRequest.getRemoteAddr());
        Experiment experiment = experimentService.createExperiment(request);
        return new ResponseEntity(mapper.map(experiment, EcaResponse.class), HttpStatus.OK);
    }

}
