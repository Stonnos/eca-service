package com.ecaservice.server.controller.web;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.server.mapping.ClassifierOptionsDatabaseModelMapper;
import com.ecaservice.server.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.server.service.classifiers.ClassifierOptionsService;
import com.ecaservice.web.dto.model.ClassifierOptionsDto;
import com.ecaservice.web.dto.model.ClassifiersOptionsPageDto;
import com.ecaservice.web.dto.model.CreateClassifierOptionsResultDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
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

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.server.controller.doc.ApiExamples.GET_CLASSIFIERS_OPTIONS_LIST_RESPONSE_JSON;
import static com.ecaservice.server.controller.doc.ApiExamples.GET_CLASSIFIERS_OPTIONS_PAGE_RESPONSE_JSON;
import static com.ecaservice.server.controller.doc.ApiExamples.SAVE_CLASSIFIER_OPTIONS_RESPONSE_JSON;
import static com.ecaservice.server.util.ClassifierOptionsHelper.parseOptions;
import static com.ecaservice.web.dto.doc.CommonApiExamples.DATA_NOT_FOUND_RESPONSE_JSON;
import static com.ecaservice.web.dto.doc.CommonApiExamples.INVALID_PAGE_REQUEST_RESPONSE_JSON;
import static com.ecaservice.web.dto.doc.CommonApiExamples.SIMPLE_PAGE_REQUEST_JSON;
import static com.ecaservice.web.dto.doc.CommonApiExamples.UNAUTHORIZED_RESPONSE_JSON;

/**
 * Implements experiment classifiers configs API for web application.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "Experiment classifiers configs API for web application")
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
    @Operation(
            description = "Finds active classifiers options configs",
            summary = "Finds active classifiers options configs",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = GET_CLASSIFIERS_OPTIONS_LIST_RESPONSE_JSON),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ClassifierOptionsDto.class))
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    )
            }
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
    @Operation(
            description = "Finds classifiers options configs page",
            summary = "Finds classifiers options configs page",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = SIMPLE_PAGE_REQUEST_JSON)
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = GET_CLASSIFIERS_OPTIONS_PAGE_RESPONSE_JSON),
                                    },
                                    schema = @Schema(implementation = ClassifiersOptionsPageDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = INVALID_PAGE_REQUEST_RESPONSE_JSON),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/page")
    public PageDto<ClassifierOptionsDto> getClassifiersOptionsPage(
            @Parameter(description = "Configuration id", example = "1", required = true)
            @RequestParam long configurationId,
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
    @Operation(
            description = "Saves new classifier options for specified configuration",
            summary = "Saves new classifier options for specified configuration",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = SAVE_CLASSIFIER_OPTIONS_RESPONSE_JSON),
                                    },
                                    schema = @Schema(implementation = CreateClassifierOptionsResultDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    )
            }
    )
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CreateClassifierOptionsResultDto save(
            @Parameter(description = "Configuration id", example = "1", required = true)
            @RequestParam long configurationId,
            @Parameter(description = "Classifiers options file", required = true)
            @RequestParam MultipartFile classifiersOptionsFile) {
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
    @Operation(
            description = "Classifier options id",
            summary = "Classifier options id",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = DATA_NOT_FOUND_RESPONSE_JSON),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @DeleteMapping(value = "/delete")
    public void delete(@Parameter(description = "Classifier options id", example = "1", required = true)
                       @RequestParam long id) {
        classifierOptionsService.deleteOptions(id);
    }


}
