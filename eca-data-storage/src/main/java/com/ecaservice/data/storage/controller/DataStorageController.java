package com.ecaservice.data.storage.controller;

import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.model.MultipartFileResource;
import com.ecaservice.data.storage.service.StorageService;
import com.ecaservice.web.dto.model.CreateInstancesResultDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

/**
 * Data storage API for web application.
 *
 * @author Roman Batygin
 */
@Validated
@Slf4j
@Api(tags = "Data storage API for web application")
@RestController
@RequestMapping("/instances")
@RequiredArgsConstructor
public class DataStorageController {

    private final StorageService storageService;

    /**
     * Saves instances into database.
     *
     * @param trainingData - training data file with format, such as csv, xls, xlsx, arff, json, docx, data, txt
     * @param tableName    - table name
     * @return create instances results dto
     */
    @ApiOperation(
            value = "Saves instances into database",
            notes = "Saves instances into database"
    )
    @PostMapping(value = "/save")
    public CreateInstancesResultDto saveInstances(
            @ApiParam(value = "Training data file", required = true) @RequestParam MultipartFile trainingData,
            @ApiParam(value = "Table name", required = true) @NotBlank @RequestParam String tableName) {
        log.info("Received request for saving instances '{}' into table [{}]",
                trainingData.getOriginalFilename(), tableName);
        CreateInstancesResultDto createInstancesResultDto = new CreateInstancesResultDto();
        createInstancesResultDto.setSourceFileName(trainingData.getOriginalFilename());
        createInstancesResultDto.setTableName(tableName);
        try {
            InstancesEntity instancesEntity =
                    storageService.saveData(new MultipartFileResource(trainingData), tableName);
            createInstancesResultDto.setCreated(true);
            createInstancesResultDto.setId(instancesEntity.getId());
        } catch (Exception ex) {
            log.error("There was an error while saving instances '{}' into table [{}]: {}",
                    trainingData.getOriginalFilename(), tableName, ex.getMessage());
            createInstancesResultDto.setErrorMessage(ex.getMessage());
        }
        return createInstancesResultDto;
    }
}
