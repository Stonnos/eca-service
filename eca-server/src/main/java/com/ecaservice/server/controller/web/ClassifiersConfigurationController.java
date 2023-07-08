package com.ecaservice.server.controller.web;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.event.model.push.RenameClassifiersConfigurationPushEvent;
import com.ecaservice.server.event.model.push.SetActiveClassifiersConfigurationPushEvent;
import com.ecaservice.server.mapping.ClassifiersConfigurationMapper;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.report.model.ClassifiersConfigurationBean;
import com.ecaservice.server.service.UserService;
import com.ecaservice.server.service.classifiers.ClassifiersConfigurationHistoryService;
import com.ecaservice.server.service.classifiers.ClassifiersConfigurationService;
import com.ecaservice.web.dto.model.ClassifierConfigurationsPageDto;
import com.ecaservice.web.dto.model.ClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.ClassifiersConfigurationHistoryDto;
import com.ecaservice.web.dto.model.ClassifiersConfigurationHistoryPageDto;
import com.ecaservice.web.dto.model.CreateClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.UpdateClassifiersConfigurationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.IOException;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;
import static com.ecaservice.core.lock.redis.config.RedisLockAutoConfiguration.REDIS_LOCK_REGISTRY;
import static com.ecaservice.server.config.audit.AuditCodes.GENERATE_CONFIGURATION_REPORT;
import static com.ecaservice.server.config.audit.AuditCodes.SET_ACTIVE_CONFIGURATION;
import static com.ecaservice.server.report.ReportTemplates.CLASSIFIERS_CONFIGURATION_TEMPLATE;
import static com.ecaservice.server.util.ReportHelper.download;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Implements API to manage experiment classifiers configurations.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "API to manage experiment classifiers configurations")
@Validated
@RestController
@RequestMapping("/experiment/classifiers-configurations")
@RequiredArgsConstructor
public class ClassifiersConfigurationController {

    private static final String CONFIGURATION_FILE_NAME_FORMAT = "%s configuration";

    private final ClassifiersConfigurationService classifiersConfigurationService;
    private final ClassifiersConfigurationHistoryService classifiersConfigurationHistoryService;
    private final ClassifiersConfigurationMapper classifiersConfigurationMapper;
    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Finds classifiers configurations with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return classifiers configurations page
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Finds classifiers configurations with specified options",
            summary = "Finds classifiers configurations with specified options",
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
                                                    name = "ClassifiersConfigurationsPageResponse",
                                                    ref = "#/components/examples/ClassifiersConfigurationsPageResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = ClassifierConfigurationsPageDto.class)
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
    @PostMapping(value = "/list")
    public PageDto<ClassifiersConfigurationDto> getClassifiersConfigurations(
            @Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received classifiers configurations page request: {}", pageRequestDto);
        return classifiersConfigurationService.getClassifiersConfigurations(pageRequestDto);
    }

    /**
     * Gets classifiers configuration details.
     *
     * @param id - configuration id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets classifiers configuration details",
            summary = "Gets classifiers configuration details",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "ClassifiersConfigurationResponse",
                                                    ref = "#/components/examples/ClassifiersConfigurationResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = ClassifiersConfigurationDto.class)
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
                                                    name = "DataNotFoundResponse",
                                                    ref = "#/components/examples/DataNotFoundResponse"
                                            )
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @GetMapping(value = "/details/{id}")
    public ClassifiersConfigurationDto getClassifiersConfigurationDetails(
            @Parameter(description = "Configuration id", example = "1", required = true)
            @Min(VALUE_1) @Max(Long.MAX_VALUE)
            @PathVariable Long id) {
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
    @Operation(
            description = "Saves new classifiers configuration",
            summary = "Saves new classifiers configuration",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "SaveClassifiersConfigurationRequest",
                                    ref = "#/components/examples/SaveClassifiersConfigurationRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "ClassifiersConfigurationResponse",
                                                    ref = "#/components/examples/ClassifiersConfigurationResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = ClassifiersConfigurationDto.class)
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
                                                    name = "EmptyConfigurationNameErrorResponse",
                                                    ref = "#/components/examples/EmptyConfigurationNameErrorResponse"
                                            )
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
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
    @Operation(
            description = "Updates classifiers configuration",
            summary = "Updates classifiers configuration",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "UpdateClassifiersConfigurationRequest",
                                    ref = "#/components/examples/UpdateClassifiersConfigurationRequest"
                            )
                    })
            }),
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
                                                    name = "EmptyConfigurationNameErrorResponse",
                                                    ref = "#/components/examples/EmptyConfigurationNameErrorResponse"
                                            )
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PutMapping(value = "/update")
    public void update(@Valid @RequestBody UpdateClassifiersConfigurationDto configurationDto) {
        var updatedConfiguration = classifiersConfigurationService.update(configurationDto);
        applicationEventPublisher.publishEvent(
                new RenameClassifiersConfigurationPushEvent(this, userService.getCurrentUser(),
                        updatedConfiguration));
    }

    /**
     * Deletes classifiers configuration by specified id.
     *
     * @param id - configuration id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Deletes classifiers configuration",
            summary = "Deletes classifiers configuration",
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
    public void delete(@Parameter(description = "Configuration id", example = "1", required = true)
                       @Min(VALUE_1) @Max(Long.MAX_VALUE)
                       @RequestParam long id) {
        classifiersConfigurationService.delete(id);
    }

    /**
     * Creates classifiers configuration copy.
     *
     * @param configurationDto - classifiers configuration
     * @return classifiers configuration dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Creates classifiers configuration copy",
            summary = "Creates classifiers configuration copy",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "UpdateClassifiersConfigurationRequest",
                                    ref = "#/components/examples/UpdateClassifiersConfigurationRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "ClassifiersConfigurationResponse",
                                                    ref = "#/components/examples/ClassifiersConfigurationResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = UpdateClassifiersConfigurationDto.class)
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
                                                    name = "EmptyConfigurationNameErrorResponse",
                                                    ref = "#/components/examples/EmptyConfigurationNameErrorResponse"
                                            )
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/copy")
    public ClassifiersConfigurationDto copy(@Valid @RequestBody UpdateClassifiersConfigurationDto configurationDto) {
        ClassifiersConfiguration copy = classifiersConfigurationService.copy(configurationDto);
        return classifiersConfigurationMapper.map(copy);
    }

    /**
     * Sets classifiers configuration as active.
     *
     * @param id - configuration id
     */
    @Locked(lockName = "setActiveClassifiersConfiguration", lockRegistry = REDIS_LOCK_REGISTRY)
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Sets classifiers configuration as active",
            summary = "Sets classifiers configuration as active",
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
    @PostMapping(value = "/set-active")
    public void setActive(@Parameter(description = "Configuration id", example = "1", required = true)
                          @Min(VALUE_1) @Max(Long.MAX_VALUE)
                          @RequestParam long id) {
        var activeConfiguration = classifiersConfigurationService.setActive(id);
        applicationEventPublisher.publishEvent(
                new SetActiveClassifiersConfigurationPushEvent(this, userService.getCurrentUser(),
                        activeConfiguration));
    }

    /**
     * Downloads classifiers configuration report in xlsx format.
     *
     * @param id                  - classifiers configuration id
     * @param httpServletResponse - http servlet response
     * @throws IOException in case of I/O error
     */
    @Audit(value = GENERATE_CONFIGURATION_REPORT, correlationIdKey = "#id")
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Downloads classifiers configuration report in xlsx format",
            summary = "Downloads classifiers configuration report in xlsx format",
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
    @GetMapping(value = "/report/{id}")
    public void downloadReport(
            @Parameter(description = "Configuration id", example = "1", required = true)
            @Min(VALUE_1) @Max(Long.MAX_VALUE) @PathVariable Long id,
            HttpServletResponse httpServletResponse) throws IOException {
        log.info("Downloads classifier configuration report by id {}", id);
        ClassifiersConfigurationBean classifiersConfigurationBean =
                classifiersConfigurationService.getClassifiersConfigurationReport(id);
        String fileName =
                String.format(CONFIGURATION_FILE_NAME_FORMAT, classifiersConfigurationBean.getConfigurationName());
        download(CLASSIFIERS_CONFIGURATION_TEMPLATE, fileName, httpServletResponse, classifiersConfigurationBean);
    }

    /**
     * Finds classifiers configuration history with specified options such as filter, sorting and paging.
     *
     * @param configurationId - configuration id
     * @param pageRequestDto  - page request dto
     * @return classifiers configurations page
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Finds classifiers configuration history with specified options",
            summary = "Finds classifiers configuration history with specified options",
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
                                                    name = "ClassifiersConfigurationHistoryPageResponse",
                                                    ref = "#/components/examples/ClassifiersConfigurationHistoryPageResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = ClassifiersConfigurationHistoryPageDto.class)
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
    @PostMapping(value = "/history")
    public PageDto<ClassifiersConfigurationHistoryDto> getClassifiersConfigurationHistory(
            @Parameter(description = "Configuration id", example = "1", required = true)
            @Min(VALUE_1) @Max(Long.MAX_VALUE)
            @RequestParam long configurationId,
            @Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received classifiers configuration history page request: {}", pageRequestDto);
        return classifiersConfigurationHistoryService.getNextPage(configurationId, pageRequestDto);
    }
}
