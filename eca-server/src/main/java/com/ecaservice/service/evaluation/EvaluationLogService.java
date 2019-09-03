package com.ecaservice.service.evaluation;

import com.ecaservice.config.CommonConfig;
import com.ecaservice.filter.EvaluationLogFilter;
import com.ecaservice.mapping.EvaluationLogDetailsMapper;
import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.projections.RequestStatusStatistics;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.repository.EvaluationResultsRequestEntityRepository;
import com.ecaservice.service.PageRequestService;
import com.ecaservice.service.ers.ErsService;
import com.ecaservice.service.filter.FilterService;
import com.ecaservice.util.SortUtils;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.ecaservice.util.Utils.buildEvaluationResultsDto;

/**
 * Evaluation log service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class EvaluationLogService implements PageRequestService<EvaluationLog> {

    private final CommonConfig commonConfig;
    private final FilterService filterService;
    private final EvaluationLogDetailsMapper evaluationLogDetailsMapper;
    private final ErsService ersService;
    private final EvaluationLogRepository evaluationLogRepository;
    private final EvaluationResultsRequestEntityRepository evaluationResultsRequestEntityRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param commonConfig                             - common config bean
     * @param filterService                            - filter service bean
     * @param evaluationLogDetailsMapper               - evaluation log details service bean
     * @param ersService                               - ers service bean
     * @param evaluationLogRepository                  - evaluation log repository bean
     * @param evaluationResultsRequestEntityRepository - evaluation results request entity repository bean
     */
    @Inject
    public EvaluationLogService(CommonConfig commonConfig,
                                FilterService filterService,
                                EvaluationLogDetailsMapper evaluationLogDetailsMapper,
                                ErsService ersService,
                                EvaluationLogRepository evaluationLogRepository,
                                EvaluationResultsRequestEntityRepository evaluationResultsRequestEntityRepository) {
        this.commonConfig = commonConfig;
        this.filterService = filterService;
        this.evaluationLogDetailsMapper = evaluationLogDetailsMapper;
        this.ersService = ersService;
        this.evaluationLogRepository = evaluationLogRepository;
        this.evaluationResultsRequestEntityRepository = evaluationResultsRequestEntityRepository;
    }

    @Override
    public Page<EvaluationLog> getNextPage(PageRequestDto pageRequestDto) {
        Sort sort = SortUtils.buildSort(pageRequestDto.getSortField(), pageRequestDto.isAscending());
        List<String> globalFilterFields = filterService.getGlobalFilterFields(FilterTemplateType.EVALUATION_LOG);
        EvaluationLogFilter filter = new EvaluationLogFilter(pageRequestDto.getSearchQuery(), globalFilterFields,
                pageRequestDto.getFilters());
        int pageSize = Integer.min(pageRequestDto.getSize(), commonConfig.getMaxPageSize());
        return evaluationLogRepository.findAll(filter, PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
    }

    /**
     * Gets evaluation log report.
     *
     * @param evaluationLog - evaluation log entity
     * @return evaluation log details dto
     */
    public EvaluationLogDetailsDto getEvaluationLogDetails(EvaluationLog evaluationLog) {
        EvaluationLogDetailsDto evaluationLogDetailsDto = evaluationLogDetailsMapper.map(evaluationLog);
        evaluationLogDetailsDto.setEvaluationResultsDto(getEvaluationResults(evaluationLog));
        return evaluationLogDetailsDto;
    }

    /**
     * Calculates requests status counting statistics.
     *
     * @return requests status counting statistics list
     */
    public Map<RequestStatus, Long> getRequestStatusesStatistics() {
        Map<RequestStatus, Long> requestStatusesMap =
                evaluationLogRepository.getRequestStatusesStatistics().stream().collect(
                        Collectors.toMap(RequestStatusStatistics::getRequestStatus,
                                RequestStatusStatistics::getRequestsCount, (v1, v2) -> v1, TreeMap::new));
        Arrays.stream(RequestStatus.values()).filter(
                requestStatus -> !requestStatusesMap.containsKey(requestStatus)).forEach(
                requestStatus -> requestStatusesMap.put(requestStatus, 0L));
        return requestStatusesMap;
    }

    private EvaluationResultsDto getEvaluationResults(EvaluationLog evaluationLog) {
        EvaluationResultsStatus evaluationResultsStatus;
        if (!RequestStatus.FINISHED.equals(evaluationLog.getEvaluationStatus())) {
            evaluationResultsStatus = RequestStatus.NEW.equals(evaluationLog.getEvaluationStatus()) ?
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
