package com.ecaservice.ers.controller.web;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.ers.service.EvaluationResultsHistoryService;
import com.ecaservice.ers.service.InstancesHistoryService;
import com.ecaservice.web.dto.model.EvaluationResultsHistoryDto;
import com.ecaservice.web.dto.model.EvaluationResultsHistoryPageDto;
import com.ecaservice.web.dto.model.FilterFieldDto;
import com.ecaservice.web.dto.model.InstancesInfoDto;
import com.ecaservice.web.dto.model.InstancesInfoPageDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;
import static com.ecaservice.ers.dictionary.FilterDictionaries.EVALUATION_RESULTS_HISTORY_TEMPLATE;

/**
 * Evaluation results API web application.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "Evaluation results history API for web application")
@RestController
@RequestMapping("/evaluation-results")
@RequiredArgsConstructor
public class EvaluationResultsHistoryController {

    private final FilterTemplateService filterTemplateService;
    private final EvaluationResultsHistoryService evaluationResultsHistoryService;
    private final InstancesHistoryService instancesHistoryService;

    /**
     * Finds evaluation results history page with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return audit logs page
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Finds evaluation results history page with specified options such as filter, sorting and paging",
            summary = "Finds evaluation results history page with specified options such as filter, sorting and paging",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "SimplePageRequest",
                                    ref = "#/components/examples/SimplePageRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "EvaluationResultsHistoryPageResponse",
                                                    ref = "#/components/examples/EvaluationResultsHistoryPageResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = EvaluationResultsHistoryPageDto.class)
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
    @PostMapping(value = "/history")
    public PageDto<EvaluationResultsHistoryDto> getEvaluationResultsHistoryPage(
            @Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received evaluation results history page request: {}", pageRequestDto);
        return evaluationResultsHistoryService.getNextPage(pageRequestDto);
    }

    /**
     * Gets evaluation results history filter fields.
     *
     * @return filter fields list
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets evaluation results history filter fields",
            summary = "Gets evaluation results history filter fields",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "EvaluationResultsHistoryFilterFieldsResponse",
                                                    ref = "#/components/examples/EvaluationResultsHistoryFilterFieldsResponse"
                                            ),
                                    }
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
    @GetMapping(value = "/filter-templates/fields")
    public List<FilterFieldDto> getEvaluationResultsHistoryFilter() {
        return filterTemplateService.getFilterFields(EVALUATION_RESULTS_HISTORY_TEMPLATE);
    }

    /**
     * Finds instances info history page with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return instances info page
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Finds instances info history page with specified options such as filter, sorting and paging",
            summary = "Finds instances info history page with specified options such as filter, sorting and paging",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "InstancesInfoPageRequest",
                                    ref = "#/components/examples/InstancesInfoPageRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "InstancesInfoPageResponse",
                                                    ref = "#/components/examples/InstancesInfoPageResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = InstancesInfoPageDto.class)
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
    @PostMapping(value = "/instances/history")
    public PageDto<InstancesInfoDto> getInstancesInfoHistoryPage(@Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received instances info history page request: {}", pageRequestDto);
        return instancesHistoryService.getNextPage(pageRequestDto);
    }
}
