package com.ecaservice.controller;

import com.ecaservice.mapping.ClassifierOptionsDatabaseModelMapper;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.service.experiment.ExperimentConfigurationService;
import com.ecaservice.web.dto.ClassifierOptionsDto;
import com.ecaservice.web.dto.PageDto;
import com.ecaservice.web.dto.PageRequestDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

/**
 * Rest controller for managing with classifiers input options config.
 *
 * @author Roman Batygin
 */
@Api(tags = "Operations for experiment classifiers configs")
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
     * Saves classifier input options config into database.
     *
     * @param classifierOptions - classifiers options for saving
     * @return response entity
     */
    @ApiOperation(
            value = "Saves classifier input options config into database",
            notes = "Saves classifier input options config into database"
    )
    @PostMapping(value = "/save")
    public ResponseEntity<ClassifierOptionsDto> save(@RequestBody ClassifierOptions classifierOptions) {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel =
                experimentConfigurationService.saveClassifierOptions(classifierOptions);
        return ResponseEntity.ok(classifierOptionsDatabaseModelMapper.map(classifierOptionsDatabaseModel));
    }

    /**
     * Finds the last classifiers options configs.
     *
     * @return response entity
     */
    @ApiOperation(
            value = "Finds the last classifiers options configs",
            notes = "Finds the last classifiers options configs"
    )
    @GetMapping(value = "/configs")
    public ResponseEntity<List<ClassifierOptionsDto>> configs() {
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                experimentConfigurationService.findLastClassifiersOptions();
        return ResponseEntity.ok(classifierOptionsDatabaseModelMapper.map(classifierOptionsDatabaseModels));
    }

    /**
     * Finds the last classifiers options configs.
     *
     * @param pageRequestDto - page request dto
     * @return response entity
     */
    @ApiOperation(
            value = "Finds the last classifiers options configs",
            notes = "Finds the last classifiers options configs"
    )
    @GetMapping(value = "/configs-page")
    public PageDto<ClassifierOptionsDto> getConfigsPage(PageRequestDto pageRequestDto) {
        Page<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                experimentConfigurationService.findLastClassifiersOptions(pageRequestDto);
        List<ClassifierOptionsDto> classifierOptionsDtoList =
                classifierOptionsDatabaseModelMapper.map(classifierOptionsDatabaseModels.getContent());
        return PageDto.of(classifierOptionsDtoList, classifierOptionsDatabaseModels.getTotalElements());
    }
}
