package com.ecaservice.controller;

import com.ecaservice.dto.EvaluationRequestDto;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.mapping.EvaluationRequestMapper;
import com.ecaservice.model.evaluation.EvaluationRequest;
import com.ecaservice.service.evaluation.EvaluationRequestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

/**
 * Implements the main rest controller for processing input requests.
 *
 * @author Roman Batygin
 */
@Api(tags = "Operations for individual and ensemble classifiers learning")
@Slf4j
@RestController
@RequestMapping("/evaluation")
public class EvaluationController {

    private final EvaluationRequestService evaluationRequestService;
    private final EvaluationRequestMapper evaluationRequestMapper;

    /**
     * Constructor with dependency spring injection.
     *
     * @param evaluationRequestService - evaluation request service bean
     * @param evaluationRequestMapper  - evaluation request mapper bean
     */
    @Inject
    public EvaluationController(EvaluationRequestService evaluationRequestService,
                                EvaluationRequestMapper evaluationRequestMapper) {
        this.evaluationRequestService = evaluationRequestService;
        this.evaluationRequestMapper = evaluationRequestMapper;
    }

    /**
     * Processed the request on classifier model evaluation.
     *
     * @param evaluationRequestDto evaluation request dto
     * @param request              http servlet request
     * @return response entity
     */
    @ApiOperation(
            value = "Evaluates classifier using specified evaluation method",
            notes = "Evaluates classifier using specified evaluation method"
    )
    @PostMapping(value = "/execute")
    public ResponseEntity<EvaluationResponse> execute(@RequestBody EvaluationRequestDto evaluationRequestDto,
                                                      HttpServletRequest request) {
        log.info("Received evaluation request for client {}", request.getRemoteAddr());
        EvaluationRequest evaluationRequest = evaluationRequestMapper.map(evaluationRequestDto);
        evaluationRequest.setIpAddress(request.getRemoteAddr());
        EvaluationResponse evaluationResponse = evaluationRequestService.processRequest(evaluationRequest);
        log.info("Evaluation response with status [{}] has been built.", evaluationResponse.getStatus());
        return ResponseEntity.ok(evaluationResponse);
    }

}
