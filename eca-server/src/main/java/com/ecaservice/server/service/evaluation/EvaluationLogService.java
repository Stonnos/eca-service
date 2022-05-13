package com.ecaservice.server.service.evaluation;

import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.filter.EvaluationLogFilter;
import com.ecaservice.server.mapping.ClassifierInfoMapper;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.projections.RequestStatusStatistics;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.EvaluationResultsRequestEntityRepository;
import com.ecaservice.server.service.PageRequestService;
import com.ecaservice.server.service.classifiers.ClassifiersTemplateService;
import com.ecaservice.server.service.ers.ErsService;
import com.ecaservice.web.dto.model.ClassifierInfoDto;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.server.model.entity.AbstractEvaluationEntity_.CREATION_DATE;
import static com.ecaservice.server.util.Utils.buildEvaluationResultsDto;
import static com.ecaservice.server.util.Utils.toRequestStatusStatisticsMap;

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
    private final ClassifierInfoMapper classifierInfoMapper;
    private final ClassifiersTemplateService classifiersTemplateService;
    private final ErsService ersService;
    private final EvaluationLogRepository evaluationLogRepository;
    private final EvaluationResultsRequestEntityRepository evaluationResultsRequestEntityRepository;

    @Override
    public Page<EvaluationLog> getNextPage(PageRequestDto pageRequestDto) {
        Sort sort = buildSort(pageRequestDto.getSortField(), CREATION_DATE, pageRequestDto.isAscending());
        List<String> globalFilterFields = filterService.getGlobalFilterFields(FilterTemplateType.EVALUATION_LOG.name());
        EvaluationLogFilter filter = new EvaluationLogFilter(pageRequestDto.getSearchQuery(), globalFilterFields,
                pageRequestDto.getFilters());
        int pageSize = Integer.min(pageRequestDto.getSize(), appProperties.getMaxPageSize());
        return evaluationLogRepository.findAll(filter, PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
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
                    var classifierInfoDto = populateClassifierInfo(evaluationLog);
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
        var classifierInfoDto = populateClassifierInfo(evaluationLog);
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

    private ClassifierInfoDto populateClassifierInfo(EvaluationLog evaluationLog) {
        if (StringUtils.isNotEmpty(evaluationLog.getClassifierInfo().getClassifierOptions())) {
            return classifiersTemplateService.processClassifierInfo(
                    evaluationLog.getClassifierInfo().getClassifierOptions());
        } else {
            //Returns classifiers options list (for old data)
            return classifierInfoMapper.map(evaluationLog.getClassifierInfo());
        }
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
