package com.ecaservice.server.controller.web;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.controller.doc.ApiExamples;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.evaluation.EvaluationLogService;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.server.util.Utils.toRequestStatusesStatistics;

/**
 * Classifiers evaluation API for web application.
 *
 * @author Roman Batygin
 */
@Tag(name = "Classifiers evaluation API for web application")
@Slf4j
@RestController
@RequestMapping("/evaluation")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationLogService evaluationLogService;
    private final EvaluationLogMapper evaluationLogMapper;
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
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = ApiExamples.EVALUATION_LOGS_PAGE_REQUEST_JSON)
                    })
            })
    )
    @PostMapping(value = "/list")
    public PageDto<EvaluationLogDto> getEvaluationLogs(@Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received evaluation logs page request: {}", pageRequestDto);
        Page<EvaluationLog> evaluationLogs = evaluationLogService.getNextPage(pageRequestDto);
        List<EvaluationLogDto> evaluationLogDtoList = evaluationLogs.getContent()
                .stream()
                .map(evaluationLogMapper::map)
                .collect(Collectors.toList());
        return PageDto.of(evaluationLogDtoList, pageRequestDto.getPage(), evaluationLogs.getTotalElements());
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
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @GetMapping(value = "/details/{id}")
    public ResponseEntity<EvaluationLogDetailsDto> getEvaluationLogDetails(
            @Parameter(description = "Evaluation log id", required = true)
            @PathVariable Long id) {
        log.info("Received request for evaluation log details for id [{}]", id);
        EvaluationLog evaluationLog = evaluationLogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EvaluationLog.class, id));
        return ResponseEntity.ok(evaluationLogService.getEvaluationLogDetails(evaluationLog));
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
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
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
