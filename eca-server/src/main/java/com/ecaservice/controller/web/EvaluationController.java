package com.ecaservice.controller.web;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.mapping.EvaluationLogMapper;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.evaluation.EvaluationLogService;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

import static com.ecaservice.util.Utils.toRequestStatusesStatistics;

/**
 * Classifiers evaluation API for web application.
 *
 * @author Roman Batygin
 */
@Api(tags = "Classifiers evaluation API for web application")
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
    @ApiOperation(
            value = "Finds evaluation logs with specified options",
            notes = "Finds evaluation logs with specified options"
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
     * @param requestId - evaluation log request id
     * @return evaluation log details report
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Gets evaluation log details",
            notes = "Gets evaluation log details"
    )
    @GetMapping(value = "/details/{requestId}")
    public ResponseEntity<EvaluationLogDetailsDto> getEvaluationLogDetails(
            @ApiParam(value = "Evaluation log request id", required = true) @PathVariable String requestId) {
        log.info("Received request for evaluation log details for request id [{}]", requestId);
        EvaluationLog evaluationLog = evaluationLogRepository.findByRequestId(requestId)
                .orElseThrow(() -> new EntityNotFoundException(EvaluationLog.class, requestId));
        return ResponseEntity.ok(evaluationLogService.getEvaluationLogDetails(evaluationLog));
    }

    /**
     * Gets evaluations request statuses statistics.
     *
     * @return evaluations request statuses statistics dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Gets evaluations request statuses statistics",
            notes = "Gets evaluations request statuses statistics"
    )
    @GetMapping(value = "/request-statuses-statistics")
    public RequestStatusStatisticsDto getEvaluationRequestStatusesStatistics() {
        return toRequestStatusesStatistics(evaluationLogService.getRequestStatusesStatistics());
    }
}
