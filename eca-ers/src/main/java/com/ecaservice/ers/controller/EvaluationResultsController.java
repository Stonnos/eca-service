package com.ecaservice.ers.controller;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.dto.EvaluationResultsResponse;
import com.ecaservice.ers.dto.GetEvaluationResultsRequest;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.ers.service.ClassifierOptionsRequestService;
import com.ecaservice.ers.service.EvaluationResultsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.ecaservice.ers.controller.doc.ApiExamples.EVALUATION_RESULTS_BAD_REQUEST_RESPONSE_JSON;
import static com.ecaservice.ers.controller.doc.ApiExamples.EVALUATION_RESULTS_REQUEST_JSON;
import static com.ecaservice.ers.controller.doc.ApiExamples.EVALUATION_RESULTS_RESPONSE_JSON;
import static com.ecaservice.ers.controller.doc.ApiExamples.GET_EVALUATION_RESULTS_BAD_REQUEST_RESPONSE_JSON;
import static com.ecaservice.ers.controller.doc.ApiExamples.GET_EVALUATION_RESULTS_REQUEST_JSON;
import static com.ecaservice.ers.controller.doc.ApiExamples.GET_EVALUATION_RESULTS_RESPONSE_JSON;
import static com.ecaservice.ers.controller.doc.ApiExamples.GET_OPTIMAL_CLASSIFIER_OPTIONS_BAD_REQUEST_RESPONSE_JSON;
import static com.ecaservice.ers.controller.doc.ApiExamples.GET_OPTIMAL_CLASSIFIER_OPTIONS_REQUEST_JSON;
import static com.ecaservice.ers.controller.doc.ApiExamples.GET_OPTIMAL_CLASSIFIER_OPTIONS_RESPONSE_JSON;

/**
 * Evaluation results service controller.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "Evaluation results storage API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EvaluationResultsController {

    private final EvaluationResultsService evaluationResultsService;
    private final ClassifierOptionsRequestService classifierOptionsRequestService;

    /**
     * Saves evaluation results report.
     *
     * @param evaluationResultsRequest - evaluation result request
     * @return evaluation results response
     */
    @Operation(
            description = "Saves evaluation results report",
            summary = "Saves evaluation results report",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = EVALUATION_RESULTS_REQUEST_JSON)
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = EVALUATION_RESULTS_RESPONSE_JSON),
                                    },
                                    schema = @Schema(implementation = EvaluationResultsResponse.class)
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = EVALUATION_RESULTS_BAD_REQUEST_RESPONSE_JSON),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/save")
    public EvaluationResultsResponse save(@Valid @RequestBody EvaluationResultsRequest evaluationResultsRequest) {
        log.info("Received request to save evaluation results report [{}]", evaluationResultsRequest.getRequestId());
        return evaluationResultsService.saveEvaluationResults(evaluationResultsRequest);
    }

    /**
     * Gets evaluation results simple report.
     *
     * @param request - get evaluation result request
     * @return evaluation results response
     */
    @Operation(
            description = "Gets evaluation results simple report",
            summary = "Gets evaluation results simple report",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = GET_EVALUATION_RESULTS_REQUEST_JSON)
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = GET_EVALUATION_RESULTS_RESPONSE_JSON),
                                    },
                                    schema = @Schema(implementation = GetEvaluationResultsResponse.class)
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = GET_EVALUATION_RESULTS_BAD_REQUEST_RESPONSE_JSON),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/results")
    public GetEvaluationResultsResponse getEvaluationResultsResponse(
            @Valid @RequestBody GetEvaluationResultsRequest request) {
        log.info("Received request to get evaluation results report [{}]", request.getRequestId());
        return evaluationResultsService.getEvaluationResultsResponse(request);
    }

    /**
     * Endpoint for searching optimal classifiers options for specified request.
     *
     * @param classifierOptionsRequest - classifier options request
     * @return classifier options response
     */
    @Operation(
            description = "Endpoint for searching optimal classifiers options for specified request",
            summary = "Endpoint for searching optimal classifiers options for specified request",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = GET_OPTIMAL_CLASSIFIER_OPTIONS_REQUEST_JSON)
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = GET_OPTIMAL_CLASSIFIER_OPTIONS_RESPONSE_JSON),
                                    },
                                    schema = @Schema(implementation = ClassifierOptionsResponse.class)
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    value = GET_OPTIMAL_CLASSIFIER_OPTIONS_BAD_REQUEST_RESPONSE_JSON),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/optimal-classifier-options")
    public ClassifierOptionsResponse findClassifierOptions(
            @Valid @RequestBody ClassifierOptionsRequest classifierOptionsRequest) {
        log.info("Received request to find optimal classifiers options for data [{}]",
                classifierOptionsRequest.getRelationName());
        return classifierOptionsRequestService.findClassifierOptions(classifierOptionsRequest);
    }
}
