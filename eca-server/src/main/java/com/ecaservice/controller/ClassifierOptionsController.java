package com.ecaservice.controller;

import com.ecaservice.mapping.ClassifierOptionsDatabaseModelMapper;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.service.experiment.ExperimentConfigurationService;
import com.ecaservice.web.dto.model.ClassifierOptionsDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

/**
 * Implements experiment classifiers configs API for web application.
 *
 * @author Roman Batygin
 */
@Api(tags = "Experiment classifiers configs API for web application")
@RestController
@RequestMapping("/experiment/classifiers-config")
public class ClassifierOptionsController {

    private final ExperimentConfigurationService experimentConfigurationService;
    private final ClassifierOptionsDatabaseModelMapper classifierOptionsDatabaseModelMapper;

    /**
     * Constructor with spring dependency injection.
     *
     * @param experimentConfigurationService       - experiment configuration service bean
     * @param classifierOptionsDatabaseModelMapper - classifier options database model mapper bean
     */
    @Inject
    public ClassifierOptionsController(ExperimentConfigurationService experimentConfigurationService,
                                       ClassifierOptionsDatabaseModelMapper classifierOptionsDatabaseModelMapper) {
        this.experimentConfigurationService = experimentConfigurationService;
        this.classifierOptionsDatabaseModelMapper = classifierOptionsDatabaseModelMapper;
    }

    /**
     * Finds the last classifiers options configs.
     *
     * @return response entity
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Finds the last classifiers options configs",
            notes = "Finds the last classifiers options configs"
    )
    @GetMapping(value = "/list")
    public List<ClassifierOptionsDto> configs() {
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                experimentConfigurationService.findLastClassifiersOptions();
        return classifierOptionsDatabaseModelMapper.map(classifierOptionsDatabaseModels);
    }

    /**
     * Finds the last classifiers options configs.
     *
     * @param pageRequestDto - page request dto
     * @return response entity
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Finds the last classifiers options configs",
            notes = "Finds the last classifiers options configs"
    )
    @GetMapping(value = "/page")
    public PageDto<ClassifierOptionsDto> getConfigsPage(PageRequestDto pageRequestDto) {
        Page<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                experimentConfigurationService.getNextPage(pageRequestDto);
        List<ClassifierOptionsDto> classifierOptionsDtoList =
                classifierOptionsDatabaseModelMapper.map(classifierOptionsDatabaseModels.getContent());
        return PageDto.of(classifierOptionsDtoList, pageRequestDto.getPage(),
                classifierOptionsDatabaseModels.getTotalElements());
    }
}
