package com.ecaservice.controller.web;

import com.ecaservice.mapping.ClassifierOptionsDatabaseModelMapper;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.service.classifiers.ClassifierOptionsService;
import com.ecaservice.web.dto.model.ClassifierOptionsDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Implements experiment classifiers configs API for web application.
 *
 * @author Roman Batygin
 */
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
    @GetMapping(value = "/page")
    public PageDto<ClassifierOptionsDto> getClassifiersOptionsPage(
            @ApiParam(value = "Configuration id", required = true) @RequestParam long configurationId,
            @Valid PageRequestDto pageRequestDto) {
        Page<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels =
                classifierOptionsService.getNextPage(configurationId, pageRequestDto);
        List<ClassifierOptionsDto> classifierOptionsDtoList =
                classifierOptionsDatabaseModelMapper.map(classifierOptionsDatabaseModels.getContent());
        return PageDto.of(classifierOptionsDtoList, pageRequestDto.getPage(),
                classifierOptionsDatabaseModels.getTotalElements());
    }

    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "",
            notes = ""
    )
    @PostMapping(value = "/save")
    public void save(@ApiParam(value = "Configuration id", required = true) @RequestParam long configurationId,
                     @RequestBody ClassifierOptions classifierOptions) {
        classifierOptionsService.saveClassifierOptions(configurationId, classifierOptions);
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
