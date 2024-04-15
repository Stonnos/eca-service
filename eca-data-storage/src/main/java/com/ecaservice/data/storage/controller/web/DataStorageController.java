package com.ecaservice.data.storage.controller.web;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.mapping.InstancesMapper;
import com.ecaservice.data.storage.model.MultipartFileResource;
import com.ecaservice.data.storage.model.report.ReportType;
import com.ecaservice.data.storage.report.InstancesReportService;
import com.ecaservice.data.storage.report.ReportsConfigurationService;
import com.ecaservice.data.storage.service.AttributeService;
import com.ecaservice.data.storage.service.InstancesLoader;
import com.ecaservice.data.storage.service.InstancesStatisticsService;
import com.ecaservice.data.storage.service.StorageService;
import com.ecaservice.web.dto.model.AttributeDto;
import com.ecaservice.web.dto.model.AttributeStatisticsDto;
import com.ecaservice.web.dto.model.CreateInstancesResultDto;
import com.ecaservice.web.dto.model.DataListPageDto;
import com.ecaservice.web.dto.model.InstancesDto;
import com.ecaservice.web.dto.model.InstancesPageDto;
import com.ecaservice.web.dto.model.InstancesReportInfoDto;
import com.ecaservice.web.dto.model.InstancesStatisticsDto;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
import org.springframework.web.multipart.MultipartFile;
import weka.core.Instances;

import javax.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.List;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Data storage API for web application.
 *
 * @author Roman Batygin
 */
@Validated
@Slf4j
@Tag(name = "Data storage API for web application")
@RestController
@RequestMapping("/instances")
@RequiredArgsConstructor
public class DataStorageController {
    private static final int MAX_TABLE_NAME_LENGTH = 30;

    private final StorageService storageService;
    private final InstancesReportService instancesReportService;
    private final AttributeService attributeService;
    private final InstancesStatisticsService instancesStatisticsService;
    private final ReportsConfigurationService reportsConfigurationService;
    private final InstancesLoader instancesLoader;
    private final InstancesMapper instancesMapper;

    /**
     * Finds instances tables with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return instances tables page
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Finds instances tables with specified options such as filter, sorting and paging",
            summary = "Finds instances tables with specified options such as filter, sorting and paging",
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
                                                    name = "InstancesPageResponse",
                                                    ref = "#/components/examples/InstancesPageResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = InstancesPageDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
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
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/list")
    public PageDto<InstancesDto> getInstancesPage(@Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received instances page request: {}", pageRequestDto);
        Page<InstancesEntity> instancesPage = storageService.getNextPage(pageRequestDto);
        List<InstancesDto> instancesDtoList = instancesMapper.map(instancesPage.getContent());
        return PageDto.of(instancesDtoList, pageRequestDto.getPage(), instancesPage.getTotalElements());
    }

    /**
     * Saves instances into database.
     *
     * @param trainingData - training data file with format, such as csv, xls, xlsx, arff, json, docx, data, txt
     * @param relationName - relation name
     * @return create instances results dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Saves instances into database",
            summary = "Saves instances into database",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "CreateInstancesResponse",
                                                    ref = "#/components/examples/CreateInstancesResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = CreateInstancesResultDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "DuplicateInstancesNameErrorCodeResponse",
                                                    ref = "#/components/examples/DuplicateInstancesNameErrorCodeResponse"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CreateInstancesResultDto saveInstances(
            @Parameter(description = "Training data file", required = true) @RequestParam MultipartFile trainingData,
            @Parameter(description = "Relation name", required = true)
            @Size(min = VALUE_1, max = MAX_TABLE_NAME_LENGTH) @RequestParam String relationName) {
        log.info("Received request for saving instances file [{}] with relation name [{}]",
                trainingData.getOriginalFilename(), relationName);
        MultipartFileResource multipartFileResource = new MultipartFileResource(trainingData);
        Instances instances = instancesLoader.load(multipartFileResource);
        InstancesEntity instancesEntity = storageService.saveData(instances, relationName);
        return CreateInstancesResultDto.builder()
                .id(instancesEntity.getId())
                .uuid(instancesEntity.getUuid())
                .sourceFileName(trainingData.getOriginalFilename())
                .relationName(instancesEntity.getRelationName())
                .build();
    }

    /**
     * Gets instances details.
     *
     * @param id - instances id
     * @return instances dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets instances details",
            summary = "Gets instances details",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "InstancesDetailsResponse",
                                                    ref = "#/components/examples/InstancesDetailsResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = InstancesDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
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
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @GetMapping(value = "/details/{id}")
    public InstancesDto getInstancesDetails(@Parameter(description = "Instances id", example = "1", required = true)
                                            @Min(VALUE_1) @Max(Long.MAX_VALUE)
                                            @PathVariable Long id) {
        log.info("Request get instances [{}] details", id);
        var instancesEntity = storageService.getById(id);
        return instancesMapper.map(instancesEntity);
    }

    /**
     * Renames data with specified id.
     *
     * @param id           - instances id
     * @param relationName - new relation name
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Renames data with specified id",
            summary = "Renames data with specified id",
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
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "DuplicateInstancesNameErrorCodeResponse",
                                                    ref = "#/components/examples/DuplicateInstancesNameErrorCodeResponse"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PutMapping(value = "/rename")
    public void rename(@Parameter(description = "Instances id", example = "1", required = true)
                       @Min(VALUE_1) @Max(Long.MAX_VALUE) @RequestParam long id,
                       @Parameter(description = "Relation name", required = true)
                       @Size(min = VALUE_1, max = MAX_TABLE_NAME_LENGTH) @RequestParam String relationName) {
        storageService.renameData(id, relationName);
    }

    /**
     * Deletes instances with specified id.
     *
     * @param id - instances id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Deletes instances with specified id",
            summary = "Deletes instances with specified id",
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
                                            ),
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
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @DeleteMapping(value = "/delete")
    public void delete(@Parameter(description = "Instances id", example = "1", required = true)
                       @Min(VALUE_1) @Max(Long.MAX_VALUE) @RequestParam long id) {
        storageService.deleteData(id);
    }

    /**
     * Finds data page for specified instances.
     *
     * @param id             - instances id
     * @param pageRequestDto - page request dto
     * @return response entity
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Finds data page for specified instances",
            summary = "Finds data page for specified instances",
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
                                                    name = "DataPageResponse",
                                                    ref = "#/components/examples/DataPageResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = DataListPageDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
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
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/data-page")
    public PageDto<List<String>> getDataPage(
            @Parameter(description = "Instances id", example = "1", required = true)
            @RequestParam @Min(VALUE_1) @Max(Long.MAX_VALUE) long id,
            @Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received data page request: {}, instances id [{}]", pageRequestDto, id);
        return storageService.getData(id, pageRequestDto);
    }

    /**
     * Gets attributes list for specified instances.
     *
     * @param id - instances id
     * @return attributes list
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets attributes list for specified instances",
            summary = "Gets attributes list for specified instances",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "AttributesListResponse",
                                                    ref = "#/components/examples/AttributesListResponse"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = AttributeDto.class))
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
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
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @GetMapping(value = "/attributes/{id}")
    public List<AttributeDto> getAttributes(@Parameter(description = "Instances id", example = "1", required = true)
                                            @PathVariable @Min(VALUE_1) @Max(Long.MAX_VALUE) Long id) {
        log.info("Received attributes request for instances [{}]", id);
        return storageService.getAttributes(id);
    }

    /**
     * Gets instances reports info.
     *
     * @return instances reports info list
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets instances reports info",
            summary = "Gets instances reports info",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "InstancesReportsInfoResponse",
                                                    ref = "#/components/examples/InstancesReportsInfoResponse"
                                            ),
                                    },
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = InstancesReportInfoDto.class))
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    )
            }
    )
    @GetMapping(value = "/reports-info")
    public List<InstancesReportInfoDto> getInstancesReportsInfo() {
        log.info("Request get instances reports info");
        var reportProperties = reportsConfigurationService.getReportProperties();
        var instancesReports = instancesMapper.mapReportPropertiesList(reportProperties);
        log.info("Fetched instances reports: {}", instancesReports);
        return instancesReports;
    }

    /**
     * Sets class attribute for specified instances.
     *
     * @param classAttributeId - class attribute id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Sets class attribute for specified instances",
            summary = "Sets class attribute for specified instances",
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
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "InvalidClassTypeErrorCode",
                                                    ref = "#/components/examples/InvalidClassTypeErrorCode"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PutMapping(value = "/set-class-attribute")
    public void setClassAttribute(@Parameter(description = "Class attribute id", example = "1", required = true)
                                  @Min(VALUE_1) @Max(Long.MAX_VALUE) @RequestParam long classAttributeId) {
        storageService.setClassAttribute(classAttributeId);
    }

    /**
     * Selects attribute for classification.
     *
     * @param id - attribute id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Selects attribute for classification",
            summary = "Selects attribute for classification",
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
                                            ),
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
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PutMapping(value = "/select-attribute")
    public void selectAttribute(@Parameter(description = "Attribute id", example = "1", required = true)
                                @Min(VALUE_1) @Max(Long.MAX_VALUE) @RequestParam long id) {
        attributeService.selectAttribute(id);
    }

    /**
     * Unselects attribute for classification.
     *
     * @param id - attribute id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Unselects attribute for classification",
            summary = "Unselects attribute for classification",
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
                                            ),
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
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PutMapping(value = "/unselect-attribute")
    public void unselectAttribute(@Parameter(description = "Attribute id", example = "1", required = true)
                                  @Min(VALUE_1) @Max(Long.MAX_VALUE) @RequestParam long id) {
        attributeService.unselectAttribute(id);
    }

    /**
     * Selects all attributes for classification.
     *
     * @param id - instances id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Selects all attributes for classification",
            summary = "Selects all attributes for classification",
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
                                            ),
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
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PutMapping(value = "/select-all-attributes")
    public void selectAllAttributes(@Parameter(description = "Instances id", example = "1", required = true)
                                    @Min(VALUE_1) @Max(Long.MAX_VALUE) @RequestParam long id) {
        storageService.selectAllAttributes(id);
    }

    /**
     * Download instances report with specified type.
     *
     * @param id                  - instances id
     * @param reportType          - report type
     * @param httpServletResponse - http servlet response
     * @throws Exception in case of error
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Download instances report with specified type",
            summary = "Download instances report with specified type",
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
                                            ),
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
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @GetMapping(value = "/download")
    public void downloadInstancesReport(
            @Parameter(description = "Instances id", example = "1", required = true)
            @RequestParam @Min(VALUE_1) @Max(Long.MAX_VALUE) long id,
            @Parameter(description = "Report type", required = true)
            @RequestParam ReportType reportType,
            HttpServletResponse httpServletResponse) throws Exception {
        log.info("Request to download instances [{}] report [{}]", id, reportType);
        var instancesEntity = storageService.getById(id);
        instancesReportService.generateInstancesReport(instancesEntity, reportType, httpServletResponse);
    }

    /**
     * Gets instances statistics.
     *
     * @param id - attribute id
     * @return attribute statistics
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets instances statistics",
            summary = "Gets instances statistics",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "InstancesStatisticsResponse",
                                                    ref = "#/components/examples/InstancesStatisticsResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = InstancesStatisticsDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
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
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @GetMapping(value = "/instances-stats/{id}")
    public InstancesStatisticsDto getInstancesStatistics(
            @Parameter(description = "Instances id", example = "1", required = true)
            @Min(VALUE_1) @Max(Long.MAX_VALUE)
            @PathVariable Long id) {
        log.info("Request get instances [{}] statistics", id);
        return instancesStatisticsService.getInstancesStatistics(id);
    }

    /**
     * Gets attribute statistics.
     *
     * @param id - attribute id
     * @return attribute statistics
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets attribute statistics",
            summary = "Gets attribute statistics",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "AttributeStatisticsResponse",
                                                    ref = "#/components/examples/AttributeStatisticsResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = AttributeStatisticsDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
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
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @GetMapping(value = "/attribute-stats/{id}")
    public AttributeStatisticsDto getAttributeStatistics(
            @Parameter(description = "Attribute id", example = "1", required = true)
            @Min(VALUE_1) @Max(Long.MAX_VALUE)
            @PathVariable Long id) {
        log.info("Request get attribute [{}] statistics", id);
        return instancesStatisticsService.getAttributeStatistics(id);
    }
}
