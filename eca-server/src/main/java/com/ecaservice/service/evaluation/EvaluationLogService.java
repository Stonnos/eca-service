package com.ecaservice.service.evaluation;

import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.projections.RequestStatusStatistics;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.filter.EvaluationLogFilter;
import com.ecaservice.service.PageRequestService;
import com.ecaservice.util.SortUtils;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Arrays;
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

    private final EvaluationLogRepository evaluationLogRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param evaluationLogRepository - evaluation log repository bean
     */
    @Inject
    public EvaluationLogService(EvaluationLogRepository evaluationLogRepository) {
        this.evaluationLogRepository = evaluationLogRepository;
    }

    @Override
    public Page<EvaluationLog> getNextPage(PageRequestDto pageRequestDto) {
        Sort sort = SortUtils.buildSort(pageRequestDto.getSortField(), pageRequestDto.isAscending());
        EvaluationLogFilter filter =
                new EvaluationLogFilter(pageRequestDto.getSearchQuery(), pageRequestDto.getFilters());
        return evaluationLogRepository.findAll(filter,
                PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort));
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
