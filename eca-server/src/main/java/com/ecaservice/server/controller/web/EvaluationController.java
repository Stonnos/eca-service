package com.ecaservice.server.controller.web;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.evaluation.EvaluationLogService;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import com.ecaservice.web.dto.model.EvaluationLogsPageDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
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
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;
import static com.ecaservice.server.util.Utils.toRequestStatusesStatistics;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Classifiers evaluation API for web application.
 *
 * @author Roman Batygin
 */
@Tag(name = "Classifiers evaluation API for web application")
@Slf4j
@Validated
@RestController
@RequestMapping("/evaluation")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationLogService evaluationLogService;
    private final EvaluationLogRepository evaluationLogRepository;

    /**
     * Finds evaluation logs with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return evaluations logs page
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Finds evaluation logs with specified options",
            summary = "Finds evaluation logs with specified options",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "EvaluationLogsPageRequest",
                                    ref = "#/components/examples/EvaluationLogsPageRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "EvaluationLogsPageResponse",
                                                    ref = "#/components/examples/EvaluationLogsPageResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = EvaluationLogsPageDto.class)
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
    public PageDto<EvaluationLogDto> getEvaluationLogs(@Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received evaluation logs page request: {}", pageRequestDto);
        return evaluationLogService.getEvaluationLogsPage(pageRequestDto);
    }

    /**
     * Gets evaluation log details.
     *
     * @param id - evaluation log id
     * @return evaluation log details report
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets evaluation log details",
            summary = "Gets evaluation log details",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "EvaluationLogDetailsResponse",
                                                    ref = "#/components/examples/EvaluationLogDetailsResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = EvaluationLogDetailsDto.class)
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
    public EvaluationLogDetailsDto getEvaluationLogDetails(
            @Parameter(description = "Evaluation log id", required = true)
            @Min(VALUE_1) @Max(Long.MAX_VALUE) @PathVariable Long id) {
        log.info("Received request for evaluation log details for id [{}]", id);
        EvaluationLog evaluationLog = evaluationLogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EvaluationLog.class, id));
        return evaluationLogService.getEvaluationLogDetails(evaluationLog);
    }

    /**
     * Gets evaluations request statuses statistics.
     *
     * @return evaluations request statuses statistics dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets evaluations request statuses statistics",
            summary = "Gets evaluations request statuses statistics",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "RequestStatusStatisticsResponse",
                                                    ref = "#/components/examples/RequestStatusStatisticsResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = RequestStatusStatisticsDto.class)
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
    @GetMapping(value = "/request-statuses-statistics")
    public RequestStatusStatisticsDto getEvaluationRequestStatusesStatistics() {
        log.info("Request get evaluations requests statuses statistics");
        var requestStatusStatisticsDto =
                toRequestStatusesStatistics(evaluationLogService.getRequestStatusesStatistics());
        log.info("Evaluations requests statuses statistics: {}", requestStatusStatisticsDto);
        return requestStatusStatisticsDto;
    }
}
