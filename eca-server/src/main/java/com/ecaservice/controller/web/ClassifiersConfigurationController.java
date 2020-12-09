package com.ecaservice.controller.web;

import com.ecaservice.mapping.ClassifiersConfigurationMapper;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.report.model.ClassifiersConfigurationBean;
import com.ecaservice.report.model.ReportType;
import com.ecaservice.service.classifiers.ClassifiersConfigurationService;
import com.ecaservice.web.dto.model.ClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.CreateClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.UpdateClassifiersConfigurationDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

import static com.ecaservice.util.ReportHelper.download;

/**
 * Implements API to manage experiment classifiers configurations.
 *
 * @author Roman Batygin
 */
@Slf4j
@Api(tags = "API to manage experiment classifiers configurations")
@RestController
@RequestMapping("/experiment/classifiers-configurations")
@RequiredArgsConstructor
public class ClassifiersConfigurationController {

    private static final String CONFIGURATION_FILE_NAME_FORMAT = "%s configuration";

    private final ClassifiersConfigurationService classifiersConfigurationService;
    private final ClassifiersConfigurationMapper classifiersConfigurationMapper;

    /**
     * Finds classifiers configurations with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return classifiers configurations page
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Finds classifiers configurations with specified options",
            notes = "Finds classifiers configurations with specified options"
    )
    @GetMapping(value = "/list")
    public PageDto<ClassifiersConfigurationDto> getClassifiersConfigurations(@Valid PageRequestDto pageRequestDto) {
        log.info("Received classifiers configurations page request: {}", pageRequestDto);
        return classifiersConfigurationService.getClassifiersConfigurations(pageRequestDto);
    }

    /**
     * Gets classifiers configuration details.
     *
     * @param id - configuration id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Gets classifiers configuration details",
            notes = "Gets classifiers configuration details"
    )
    @GetMapping(value = "/details/{id}")
    public ClassifiersConfigurationDto getClassifiersConfigurationDetails(
            @ApiParam(value = "Configuration id", example = "1", required = true) @PathVariable Long id) {
        log.info("Gets classifier configuration details by id {}", id);
        return classifiersConfigurationService.getClassifiersConfigurationDetails(id);
    }

    /**
     * Saves new classifiers configuration.
     *
     * @param configurationDto - classifiers configuration
     * @return classifiers configuration dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Saves new classifiers configuration",
            notes = "Saves new classifiers configuration"
    )
    @PostMapping(value = "/save")
    public ClassifiersConfigurationDto save(@Valid @RequestBody CreateClassifiersConfigurationDto configurationDto) {
        ClassifiersConfiguration classifiersConfiguration = classifiersConfigurationService.save(configurationDto);
        return classifiersConfigurationMapper.map(classifiersConfiguration);
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
    public void update(@Valid @RequestBody UpdateClassifiersConfigurationDto configurationDto) {
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

    /**
     * Downloads classifiers configuration report in xlsx format.
     *
     * @param id                  - classifiers configuration id
     * @param httpServletResponse - http servlet response
     * @throws IOException in case of I/O error
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Downloads classifiers configuration report in xlsx format",
            notes = "Downloads classifiers configuration report in xlsx format"
    )
    @GetMapping(value = "/report/{id}")
    public void downloadReport(
            @ApiParam(value = "Configuration id", example = "1", required = true) @PathVariable Long id,
            HttpServletResponse httpServletResponse) throws IOException {
        log.info("Downloads classifier configuration report by id {}", id);
        ClassifiersConfigurationBean classifiersConfigurationBean =
                classifiersConfigurationService.getClassifiersConfigurationReport(id);
        String fileName =
                String.format(CONFIGURATION_FILE_NAME_FORMAT, classifiersConfigurationBean.getConfigurationName());
        download(ReportType.CLASSIFIERS_CONFIGURATION, fileName, httpServletResponse, classifiersConfigurationBean);
    }
}
