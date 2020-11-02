package com.ecaservice.external.api.controller;

import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.service.EvaluationApiService;
import com.ecaservice.external.api.service.MessageCorrelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

/**
 * External API controller.
 *
 * @author Roman Batygin
 */
@Api(tags = "Operations for external API")
@Slf4j
@RestController
@RequestMapping("/external")
@RequiredArgsConstructor
public class ExternalApiController {

    private final MessageCorrelationService messageCorrelationService;
    private final EvaluationApiService evaluationApiService;

    /**
     * Processes evaluation request.
     *
     * @param evaluationRequestDto - evaluation request dto.
     * @return evaluation response mono object
     */
    @PreAuthorize("#oauth2.hasScope('external-api')")
    @ApiOperation(
            value = "Processes evaluation request",
            notes = "Processes evaluation request"
    )
    @PostMapping(value = "/evaluate")
    public Mono<EvaluationResponseDto> evaluateModel(@Valid EvaluationRequestDto evaluationRequestDto) {
        return Mono.create(sink -> {
            String correlationId = UUID.randomUUID().toString();
            messageCorrelationService.push(correlationId, sink);
            evaluationApiService.processRequest(correlationId, evaluationRequestDto);
        });
    }
}
