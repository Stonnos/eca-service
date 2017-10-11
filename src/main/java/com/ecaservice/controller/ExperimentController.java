package com.ecaservice.controller;

import com.ecaservice.service.experiment.ExperimentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;

/**
 * @author Roman Batygin
 */
@Slf4j
@Controller
@RequestMapping("/eca-service/experiment")
public class ExperimentController {

    private final ExperimentService experimentService;

    @Autowired
    public ExperimentController(ExperimentService experimentService) {
        this.experimentService = experimentService;
    }

    @RequestMapping(value = "/download/{uuid}", method = RequestMethod.GET)
    public void download(@PathVariable String uuid, HttpServletResponse response) {
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

}
