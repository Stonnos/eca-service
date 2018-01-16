package com.ecaservice.controller;

import com.ecaservice.dto.ClassifierOptionsDto;
import com.ecaservice.mapping.ClassifierOptionsDatabaseModelMapper;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.service.experiment.ExperimentConfigurationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Rest controller for managing with classifiers input options config.
 *
 * @author Roman Batygin
 */
@Api(tags = "Operations for experiment classifiers configs")
@RestController
@RequestMapping("/eca-service/experiment/classifiers-config")
public class ClassifierOptionsController {

    private final ExperimentConfigurationService experimentConfigurationService;
    private final ClassifierOptionsDatabaseModelMapper classifierOptionsDatabaseModelMapper;

    @Autowired
    public ClassifierOptionsController(ExperimentConfigurationService experimentConfigurationService,
                                       ClassifierOptionsDatabaseModelMapper classifierOptionsDatabaseModelMapper) {
        this.experimentConfigurationService = experimentConfigurationService;
        this.classifierOptionsDatabaseModelMapper = classifierOptionsDatabaseModelMapper;
    }

    /**
     * Saves classifier input options config into database.
     *
     * @param classifierOptions {@link ClassifierOptions} object
     * @return {@link ResponseEntity} object
     */
    @ApiOperation(
            value = "Saves classifier input options config into database",
            notes = "Saves classifier input options config into database"
    )
    @PostMapping(value = "/save")
    public ResponseEntity<ClassifierOptionsDto> save(@RequestBody ClassifierOptions classifierOptions) {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel =
                experimentConfigurationService.saveClassifierOptions(classifierOptions);
        return new ResponseEntity<>(classifierOptionsDatabaseModelMapper.map(classifierOptionsDatabaseModel),
                HttpStatus.OK);
    }

    /**
     * Finds the last classifiers options configs.
     *
     * @return {@link ResponseEntity} object.
     */
    @ApiOperation(
            value = "Finds the last classifiers options configs",
            notes = "Finds the last classifiers options configs"
    )
    @GetMapping(value = "/configs")
    public ResponseEntity<List<ClassifierOptionsDto>> configs() {
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                experimentConfigurationService.findLastClassifiersOptions();
        return new ResponseEntity<>(classifierOptionsDatabaseModelMapper.map(classifierOptionsDatabaseModels),
                HttpStatus.OK);
    }
}
