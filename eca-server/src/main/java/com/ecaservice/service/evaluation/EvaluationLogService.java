package com.ecaservice.service.evaluation;

import com.ecaservice.config.CommonConfig;
import com.ecaservice.filter.EvaluationLogFilter;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.projections.RequestStatusStatistics;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.PageRequestService;
import com.ecaservice.service.filter.GlobalFilterService;
import com.ecaservice.util.SortUtils;
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
import java.util.stream.Collectors;

/**
 * Evaluation log service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class EvaluationLogService implements PageRequestService<EvaluationLog> {

    private final CommonConfig commonConfig;
    private final GlobalFilterService globalFilterService;
    private final EvaluationLogRepository evaluationLogRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param commonConfig            - common config bean
     * @param globalFilterService     - global filter service bean
     * @param evaluationLogRepository - evaluation log repository bean
     */
    @Inject
    public EvaluationLogService(CommonConfig commonConfig,
                                GlobalFilterService globalFilterService,
                                EvaluationLogRepository evaluationLogRepository) {
        this.commonConfig = commonConfig;
        this.globalFilterService = globalFilterService;
        this.evaluationLogRepository = evaluationLogRepository;
    }

    @Override
    public Page<EvaluationLog> getNextPage(PageRequestDto pageRequestDto) {
        Sort sort = SortUtils.buildSort(pageRequestDto.getSortField(), pageRequestDto.isAscending());
        List<String> globalFilterFields = globalFilterService.getGlobalFilterFields(FilterTemplateType.EVALUATION_LOG);
        EvaluationLogFilter filter = new EvaluationLogFilter(pageRequestDto.getSearchQuery(), globalFilterFields,
                pageRequestDto.getFilters());
        int pageSize = Integer.min(pageRequestDto.getSize(), commonConfig.getMaxPageSize());
        return evaluationLogRepository.findAll(filter, PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
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
                                RequestStatusStatistics::getRequestsCount));
        Arrays.stream(RequestStatus.values()).filter(
                requestStatus -> !requestStatusesMap.containsKey(requestStatus)).forEach(
                requestStatus -> requestStatusesMap.put(requestStatus, 0L));
        return requestStatusesMap;
    }
}
