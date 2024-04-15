package com.ecaservice.server.controller.web;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.common.web.error.CommonErrorCode;
import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.server.event.model.push.AddClassifierOptionsPushEvent;
import com.ecaservice.server.event.model.push.DeleteClassifierOptionsPushEvent;
import com.ecaservice.server.service.UserService;
import com.ecaservice.server.service.classifiers.ClassifierOptionsService;
import com.ecaservice.server.service.classifiers.ClassifiersConfigurationService;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.InputStream;
import java.util.Collections;

import static com.ecaservice.common.web.ExceptionResponseHandler.handleConstraintViolation;
import static com.ecaservice.common.web.util.ValidationErrorHelper.buildValidationError;
import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;
import static com.ecaservice.server.util.ClassifierOptionsHelper.parseOptions;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Implements experiment classifiers configs API for web application.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "Experiment classifiers configs API for web application")
@Validated
@RestController
@RequestMapping("/experiment/classifiers-options")
@RequiredArgsConstructor
public class ClassifierOptionsController {

    private final ClassifierOptionsService classifierOptionsService;
    private final ClassifiersConfigurationService classifiersConfigurationService;
    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;

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
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "PageRequest",
                                    ref = "#/components/examples/PageRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "ClassifierOptionsPageResponse",
                                                    ref = "#/components/examples/ClassifierOptionsPageResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = ClassifiersOptionsPageDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "BadPageRequestResponse",
                                                    ref = "#/components/examples/BadPageRequestResponse"
                                            )
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/page")
    public PageDto<ClassifierOptionsDto> getClassifiersOptionsPage(
            @Parameter(description = "Configuration id", example = "1", required = true)
            @Min(VALUE_1) @Max(Long.MAX_VALUE)
            @RequestParam long configurationId,
            @Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received classifiers options page request: {}, configuration id [{}]", pageRequestDto,
                configurationId);
        return classifierOptionsService.getNextPage(configurationId, pageRequestDto);
    }

    /**
     * Uploads new classifier options file for specified configuration.
     *
     * @param configurationId        - configuration id
     * @param classifiersOptionsFile - classifier options file
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Uploads new classifier options file for specified configuration",
            summary = "Uploads new classifier options file for specified configuration",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "UploadClassifierOptionsResponse",
                                                    ref = "#/components/examples/UploadClassifierOptionsResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = CreateClassifierOptionsResultDto.class)
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "DataNotFoundResponse",
                                                    ref = "#/components/examples/DataNotFoundResponse"
                                            )
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            )
                                    }
                            )
                    )
            }
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CreateClassifierOptionsResultDto upload(
            @Parameter(description = "Configuration id", example = "1", required = true)
            @Min(VALUE_1) @Max(Long.MAX_VALUE)
            @RequestParam long configurationId,
            @Parameter(description = "Classifiers options file", required = true)
            @RequestParam MultipartFile classifiersOptionsFile) {
        log.info("Received request to upload classifier options for configuration id [{}], options file [{}]",
                configurationId, classifiersOptionsFile.getOriginalFilename());
        CreateClassifierOptionsResultDto classifierOptionsResultDto = new CreateClassifierOptionsResultDto();
        classifierOptionsResultDto.setSourceFileName(classifiersOptionsFile.getOriginalFilename());
        try {
            @Cleanup InputStream inputStream = classifiersOptionsFile.getInputStream();
            ClassifierOptions classifierOptions = parseOptions(inputStream);
            var classifierOptionsDto =
                    classifierOptionsService.saveClassifierOptions(configurationId, classifierOptions);
            pushAddOptionsEvent(configurationId, classifierOptionsDto);
            classifierOptionsResultDto.setId(classifierOptionsDto.getId());
            classifierOptionsResultDto.setSuccess(true);
        } catch (ValidationErrorException ex) {
            log.error("Validation error [{}] for classifier options file [{}], configuration id [{}]: {}",
                    ex.getErrorDetails().getCode(), classifiersOptionsFile.getOriginalFilename(), configurationId,
                    ex.getMessage());
            var validationErrorDto = buildValidationError(ex.getErrorDetails(), ex.getMessage());
            classifierOptionsResultDto.setValidationErrors(Collections.singletonList(validationErrorDto));
        } catch (ConstraintViolationException ex) {
            log.error("Constraint violation error while save options file [{}] for configuration id [{}]: {}",
                    classifiersOptionsFile.getOriginalFilename(), configurationId, ex.getMessage());
            var validationErrors = handleConstraintViolation(ex);
            classifierOptionsResultDto.setValidationErrors(validationErrors);
        } catch (Exception ex) {
            log.error("There was an error while save classifier options file [{}] for configuration id [{}]: {}",
                    classifiersOptionsFile.getOriginalFilename(), configurationId, ex.getMessage());
            var validationErrorDto =
                    buildValidationError(CommonErrorCode.INTERNAL_ERROR, "Unknown error");
            classifierOptionsResultDto.setValidationErrors(Collections.singletonList(validationErrorDto));
        }
        return classifierOptionsResultDto;
    }

    /**
     * Adds new classifier options for specified configuration.
     *
     * @param configurationId   - configuration id
     * @param classifierOptions - classifier options
     * @return classifier options dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Adds new classifier options for specified configuration",
            summary = "Adds new classifier options for specified configuration",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "AddClassifierOptionsRequest",
                                    ref = "#/components/examples/AddClassifierOptionsRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "ClassifierOptionsDetailsResponse",
                                                    ref = "#/components/examples/ClassifierOptionsDetailsResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = ClassifierOptionsDto.class)
                            )),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "DataNotFoundResponse",
                                                    ref = "#/components/examples/DataNotFoundResponse"
                                            )
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/add")
    public ClassifierOptionsDto addClassifierOptions(
            @Parameter(description = "Configuration id", example = "1", required = true)
            @Min(VALUE_1) @Max(Long.MAX_VALUE)
            @RequestParam long configurationId,
            @RequestBody ClassifierOptions classifierOptions) {
        log.info("Received request to save classifier options {} for configuration id [{}]", classifierOptions,
                configurationId);
        var classifierOptionsDto = classifierOptionsService.saveClassifierOptions(configurationId, classifierOptions);
        pushAddOptionsEvent(configurationId, classifierOptionsDto);
        return classifierOptionsDto;
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
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "DataNotFoundResponse",
                                                    ref = "#/components/examples/DataNotFoundResponse"
                                            )
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @DeleteMapping(value = "/delete")
    public void delete(@Parameter(description = "Classifier options id", example = "1", required = true)
                       @Min(VALUE_1) @Max(Long.MAX_VALUE)
                       @RequestParam long id) {
        var deletedOptions = classifierOptionsService.deleteOptions(id);
        var pushEvent = new DeleteClassifierOptionsPushEvent(this, userService.getCurrentUser(),
                deletedOptions.getConfiguration(), deletedOptions.getId(), deletedOptions.getOptionsName());
        applicationEventPublisher.publishEvent(pushEvent);
    }

    private void pushAddOptionsEvent(long configurationId, ClassifierOptionsDto classifierOptionsDto) {
        var classifiersConfiguration = classifiersConfigurationService.getById(configurationId);
        var event = new AddClassifierOptionsPushEvent(this, userService.getCurrentUser(),
                classifiersConfiguration, classifierOptionsDto.getId(), classifierOptionsDto.getOptionsName());
        applicationEventPublisher.publishEvent(event);
    }

}
