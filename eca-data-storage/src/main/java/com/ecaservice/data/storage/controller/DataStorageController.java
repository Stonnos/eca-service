package com.ecaservice.data.storage.controller;

import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.mapping.InstancesMapper;
import com.ecaservice.data.storage.model.MultipartFileResource;
import com.ecaservice.data.storage.service.StorageService;
import com.ecaservice.data.storage.validation.annotations.UniqueTableName;
import com.ecaservice.web.dto.model.CreateInstancesResultDto;
import com.ecaservice.web.dto.model.InstancesDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.ValidationErrorDto;
import com.google.common.collect.Iterables;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

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

    private static final String TABLE_NAME_REGEX = "^[0-9a-z_]+$";
    private static final int MAX_TABLE_NAME_LENGTH = 30;

    private final StorageService storageService;
    private final InstancesMapper instancesMapper;

    /**
     * Finds instances tables with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return instances tables page
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Finds instances tables with specified options such as filter, sorting and paging",
            notes = "Finds instances tables with specified options such as filter, sorting and paging"
    )
    @GetMapping(value = "/list")
    public PageDto<InstancesDto> getInstancesPage(@Valid PageRequestDto pageRequestDto) {
        log.info("Received instances page request: {}", pageRequestDto);
        Page<InstancesEntity> instancesPage = storageService.getNextPage(pageRequestDto);
        List<InstancesDto> instancesDtoList = instancesMapper.map(instancesPage.getContent());
        return PageDto.of(instancesDtoList, pageRequestDto.getPage(), instancesPage.getTotalElements());
    }

    /**
     * Saves instances into database.
     *
     * @param trainingData - training data file with format, such as csv, xls, xlsx, arff, json, docx, data, txt
     * @param tableName    - table name
     * @return create instances results dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Saves instances into database",
            notes = "Saves instances into database"
    )
    @PostMapping(value = "/save")
    public CreateInstancesResultDto saveInstances(
            @ApiParam(value = "Training data file", required = true) @RequestParam MultipartFile trainingData,
            @ApiParam(value = "Table name", required = true)
            @Pattern(regexp = TABLE_NAME_REGEX)
            @Size(max = MAX_TABLE_NAME_LENGTH)
            @UniqueTableName @RequestParam String tableName) {
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

    /**
     * Renames data with specified id.
     *
     * @param id        - instances id
     * @param tableName - new table name
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Renames data with specified id",
            notes = "Renames data with specified id"
    )
    @PutMapping(value = "/rename")
    public void rename(@ApiParam(value = "Instances id", example = "1", required = true) @RequestParam long id,
                       @ApiParam(value = "Table name", required = true)
                       @Pattern(regexp = TABLE_NAME_REGEX)
                       @Size(max = MAX_TABLE_NAME_LENGTH)
                       @UniqueTableName @RequestParam String tableName) {
        storageService.renameData(id, tableName);
    }

    /**
     * Deletes instances with specified id.
     *
     * @param id - instances id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Deletes instances with specified id",
            notes = "Deletes instances with specified id"
    )
    @DeleteMapping(value = "/delete")
    public void delete(@ApiParam(value = "Instances id", example = "1", required = true) @RequestParam long id) {
        storageService.deleteData(id);
    }

    /**
     * Handles constraint violation error.
     *
     * @param ex - constraint violation exception
     * @return response entity
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    protected ResponseEntity<List<ValidationErrorDto>> handleConstraintViolation(ConstraintViolationException ex) {
        List<ValidationErrorDto> validationErrors = ex.getConstraintViolations().stream()
                .map(constraintViolation -> {
                    Path.Node node = Iterables.getLast(constraintViolation.getPropertyPath());
                    ValidationErrorDto validationErrorDto = new ValidationErrorDto();
                    validationErrorDto.setFieldName(node.getName());
                    validationErrorDto.setCode(
                            constraintViolation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName());
                    validationErrorDto.setErrorMessage(constraintViolation.getMessage());
                    return validationErrorDto;
                }).collect(Collectors.toList());
        return ResponseEntity.badRequest().body(validationErrors);
    }
}
