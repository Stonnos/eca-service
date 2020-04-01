package com.ecaservice.controller.web;

import com.ecaservice.service.classifiers.ClassifiersConfigurationService;
import com.ecaservice.web.dto.model.CreateClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.UpdateClassifiersConfigurationDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Implements API to manage experiment classifiers configurations.
 *
 * @author Roman Batygin
 */
@Api(tags = "API to manage experiment classifiers configurations")
@RestController
@RequestMapping("/experiment/classifiers-configurations")
@RequiredArgsConstructor
public class ClassifiersConfigurationController {

    private final ClassifiersConfigurationService classifiersConfigurationService;

    /**
     * Saves new classifiers configuration.
     *
     * @param configurationDto - classifiers configuration
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Saves new classifiers configuration",
            notes = "Saves new classifiers configuration"
    )
    @PostMapping(value = "/save")
    public void save(@Valid CreateClassifiersConfigurationDto configurationDto) {
        classifiersConfigurationService.save(configurationDto);
    }

    /**
     * Updates classifiers configuration.
     *
     * @param configurationDto - classifiers configuration
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Updates classifiers configuration",
            notes = "Updates classifiers configuration"
    )
    @PutMapping(value = "/update")
    public void update(@Valid UpdateClassifiersConfigurationDto configurationDto) {
        classifiersConfigurationService.update(configurationDto);
    }

    /**
     * Deletes classifiers configuration by specified id.
     *
     * @param id - configuration id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Deletes classifiers configuration",
            notes = "Deletes classifiers configuration"
    )
    @DeleteMapping(value = "/delete")
    public void delete(@ApiParam(value = "Configuration id", example = "1", required = true) @RequestParam long id) {
        classifiersConfigurationService.delete(id);
    }

    /**
     * Sets classifiers configuration as active.
     *
     * @param id - configuration id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Sets classifiers configuration as active",
            notes = "Sets classifiers configuration as active"
    )
    @PostMapping(value = "/set-active")
    public void setActive(@ApiParam(value = "Configuration id", example = "1", required = true) @RequestParam long id) {
        classifiersConfigurationService.setActive(id);
    }
}
