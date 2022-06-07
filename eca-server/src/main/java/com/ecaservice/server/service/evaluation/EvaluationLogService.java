package com.ecaservice.server.service.evaluation;

import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.core.filter.specification.FilterFieldCustomizer;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.filter.EvaluationLogFilter;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.projections.RequestStatusStatistics;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.EvaluationResultsRequestEntityRepository;
import com.ecaservice.server.service.classifiers.ClassifierOptionsProcessor;
import com.ecaservice.server.service.PageRequestService;
import com.ecaservice.server.service.ers.ErsService;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.server.model.entity.AbstractEvaluationEntity_.CREATION_DATE;
import static com.ecaservice.server.util.Utils.buildEvaluationResultsDto;
import static com.ecaservice.server.util.Utils.toRequestStatusStatisticsMap;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Evaluation log service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationLogService implements PageRequestService<EvaluationLog> {

    private final AppProperties appProperties;
    private final FilterService filterService;
    private final EvaluationLogMapper evaluationLogMapper;
    private final ClassifierOptionsProcessor classifierOptionsProcessor;
    private final ErsService ersService;
    private final EvaluationLogRepository evaluationLogRepository;
    private final EvaluationResultsRequestEntityRepository evaluationResultsRequestEntityRepository;

    private final List<FilterFieldCustomizer> globalFilterFieldCustomizers = newArrayList();

    /**
     * Initialization method.
     */
    @PostConstruct
    public void initialize() {
        globalFilterFieldCustomizers.add(new ClassifierNameFilterFieldCustomizer(filterService));
    }

    @Override
    public Page<EvaluationLog> getNextPage(PageRequestDto pageRequestDto) {
        log.info("Gets evaluation logs next page: {}", pageRequestDto);
        Sort sort = buildSort(pageRequestDto.getSortField(), CREATION_DATE, pageRequestDto.isAscending());
        List<String> globalFilterFields = filterService.getGlobalFilterFields(FilterTemplateType.EVALUATION_LOG.name());
        var filter = new EvaluationLogFilter(pageRequestDto.getSearchQuery(), globalFilterFields,
                pageRequestDto.getFilters());
        filter.setGlobalFilterFieldsCustomizers(globalFilterFieldCustomizers);
        int pageSize = Integer.min(pageRequestDto.getSize(), appProperties.getMaxPageSize());
        var evaluationLogsPage =
                evaluationLogRepository.findAll(filter, PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
        log.info("Evaluation logs page [{} of {}] with size [{}] has been fetched for page request [{}]",
                evaluationLogsPage.getNumber(), evaluationLogsPage.getTotalPages(),
                evaluationLogsPage.getNumberOfElements(), pageRequestDto);
        return evaluationLogsPage;
    }

    /**
     * Gets evaluation logs page.
     *
     * @param pageRequestDto - pafe request dto
     * @return evaluation logs page
     */
    public PageDto<EvaluationLogDto> getEvaluationLogsPage(PageRequestDto pageRequestDto) {
        var evaluationLogsPage = getNextPage(pageRequestDto);
        List<EvaluationLogDto> evaluationLogDtoList = evaluationLogsPage.getContent()
                .stream()
                .map(evaluationLog -> {
                    var evaluationLogDto = evaluationLogMapper.map(evaluationLog);
                    var classifierInfoDto =
                            classifierOptionsProcessor.processClassifierInfo(evaluationLog.getClassifierInfo());
                    evaluationLogDto.setClassifierInfo(classifierInfoDto);
                    return evaluationLogDto;
                })
                .collect(Collectors.toList());
        return PageDto.of(evaluationLogDtoList, pageRequestDto.getPage(), evaluationLogsPage.getTotalElements());
    }

    /**
     * Gets evaluation log report.
     *
     * @param evaluationLog - evaluation log entity
     * @return evaluation log details dto
     */
    public EvaluationLogDetailsDto getEvaluationLogDetails(EvaluationLog evaluationLog) {
        var evaluationLogDetailsDto = evaluationLogMapper.mapDetails(evaluationLog);
        var classifierInfoDto = classifierOptionsProcessor.processClassifierInfo(evaluationLog.getClassifierInfo());
        evaluationLogDetailsDto.setClassifierInfo(classifierInfoDto);
        evaluationLogDetailsDto.setEvaluationResultsDto(getEvaluationResults(evaluationLog));
        return evaluationLogDetailsDto;
    }

    /**
     * Calculates evaluation statuses counting statistics.
     *
     * @return requests status counting statistics list
     */
    public Map<RequestStatus, Long> getRequestStatusesStatistics() {
        List<RequestStatusStatistics> requestStatusStatistics = evaluationLogRepository.getRequestStatusesStatistics();
        return toRequestStatusStatisticsMap(requestStatusStatistics);
    }

    private EvaluationResultsDto getEvaluationResults(EvaluationLog evaluationLog) {
        EvaluationResultsStatus evaluationResultsStatus;
        if (!RequestStatus.FINISHED.equals(evaluationLog.getRequestStatus())) {
            evaluationResultsStatus = RequestStatus.IN_PROGRESS.equals(evaluationLog.getRequestStatus()) ?
                    EvaluationResultsStatus.EVALUATION_IN_PROGRESS : EvaluationResultsStatus.EVALUATION_ERROR;
        } else {
            EvaluationResultsRequestEntity evaluationResultsRequestEntity =
                    evaluationResultsRequestEntityRepository.findByEvaluationLog(evaluationLog);
            if (evaluationResultsRequestEntity == null ||
                    !ErsResponseStatus.SUCCESS.equals(evaluationResultsRequestEntity.getResponseStatus())) {
                evaluationResultsStatus = EvaluationResultsStatus.RESULTS_NOT_SENT;
            } else {
                return ersService.getEvaluationResultsFromErs(evaluationResultsRequestEntity.getRequestId());
            }
        }
        return buildEvaluationResultsDto(evaluationResultsStatus);
    }
}
