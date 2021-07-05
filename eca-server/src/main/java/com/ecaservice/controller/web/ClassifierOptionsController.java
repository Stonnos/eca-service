package com.ecaservice.controller.web;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.mapping.ClassifierOptionsDatabaseModelMapper;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.service.classifiers.ClassifierOptionsService;
import com.ecaservice.web.dto.model.ClassifierOptionsDto;
import com.ecaservice.web.dto.model.CreateClassifierOptionsResultDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.InputStream;
import java.util.List;

import static com.ecaservice.util.ClassifierOptionsHelper.parseOptions;

/**
 * Implements experiment classifiers configs API for web application.
 *
 * @author Roman Batygin
 */
@Slf4j
@Api(tags = "Experiment classifiers configs API for web application")
@RestController
@RequestMapping("/experiment/classifiers-options")
@RequiredArgsConstructor
public class ClassifierOptionsController {

    private final ClassifierOptionsService classifierOptionsService;
    private final ClassifierOptionsDatabaseModelMapper classifierOptionsDatabaseModelMapper;

    /**
     * Finds active classifiers options configs.
     *
     * @return response entity
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Finds active classifiers options configs",
            notes = "Finds active classifiers options configs"
    )
    @GetMapping(value = "/active-options")
    public List<ClassifierOptionsDto> getActiveClassifiersOptions() {
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                classifierOptionsService.getActiveClassifiersOptions();
        return classifierOptionsDatabaseModelMapper.map(classifierOptionsDatabaseModels);
    }

    /**
     * Finds classifiers options configs page.
     *
     * @param pageRequestDto - page request dto
     * @return response entity
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Finds classifiers options configs page",
            notes = "Finds classifiers options configs page"
    )
    @PostMapping(value = "/page")
    public PageDto<ClassifierOptionsDto> getClassifiersOptionsPage(
            @ApiParam(value = "Configuration id", example = "1", required = true) @RequestParam long configurationId,
            @Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received classifiers options page request: {}, configuration id [{}]", pageRequestDto,
                configurationId);
        Page<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                classifierOptionsService.getNextPage(configurationId, pageRequestDto);
        List<ClassifierOptionsDto> classifierOptionsDtoList =
                classifierOptionsDatabaseModelMapper.map(classifierOptionsDatabaseModels.getContent());
        return PageDto.of(classifierOptionsDtoList, pageRequestDto.getPage(),
                classifierOptionsDatabaseModels.getTotalElements());
    }

    /**
     * Saves new classifier options for specified configuration.
     *
     * @param configurationId        - configuration id
     * @param classifiersOptionsFile - classifier options file
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Saves new classifier options for specified configuration",
            notes = "Saves new classifier options for specified configuration"
    )
    @PostMapping(value = "/save")
    public CreateClassifierOptionsResultDto save(
            @ApiParam(value = "Configuration id", example = "1", required = true) @RequestParam long configurationId,
            @ApiParam(value = "Classifiers options file", required = true) @RequestParam
                    MultipartFile classifiersOptionsFile) {
        log.info("Received request to save classifier options for configuration id [{}], options file [{}]",
                configurationId, classifiersOptionsFile.getOriginalFilename());
        CreateClassifierOptionsResultDto classifierOptionsResultDto = new CreateClassifierOptionsResultDto();
        classifierOptionsResultDto.setSourceFileName(classifiersOptionsFile.getOriginalFilename());
        try {
            @Cleanup InputStream inputStream = classifiersOptionsFile.getInputStream();
            ClassifierOptions classifierOptions = parseOptions(inputStream);
            ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel =
                    classifierOptionsService.saveClassifierOptions(configurationId, classifierOptions);
            classifierOptionsResultDto.setId(classifierOptionsDatabaseModel.getId());
            classifierOptionsResultDto.setSuccess(true);
        } catch (Exception ex) {
            log.error("There was an error while classifier options saving for configuration id [{}], options file [{}]",
                    configurationId, classifiersOptionsFile.getOriginalFilename());
            classifierOptionsResultDto.setErrorMessage(ex.getMessage());
        }
        return classifierOptionsResultDto;
    }

    /**
     * Deletes classifier options specified id.
     *
     * @param id - classifier options id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Classifier options id",
            notes = "Classifier options id"
    )
    @DeleteMapping(value = "/delete")
    public void delete(
            @ApiParam(value = "Classifier options id", example = "1", required = true) @RequestParam long id) {
        classifierOptionsService.deleteOptions(id);
    }


}
