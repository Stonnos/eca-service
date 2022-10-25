package com.ecaservice.server.service.evaluation;

import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.core.filter.specification.FilterFieldCustomizer;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.filter.EvaluationLogFilter;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.model.entity.ClassifierInfo;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.projections.RequestStatusStatistics;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.EvaluationResultsRequestEntityRepository;
import com.ecaservice.server.service.PageRequestService;
import com.ecaservice.server.service.classifiers.ClassifierOptionsProcessor;
import com.ecaservice.server.service.ers.ErsService;
import com.ecaservice.server.service.filter.dictionary.FilterDictionaries;
import com.ecaservice.web.dto.model.ChartDataDto;
import com.ecaservice.web.dto.model.ChartDto;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.server.model.entity.AbstractEvaluationEntity_.CREATION_DATE;
import static com.ecaservice.server.model.entity.ClassifierInfo_.CLASSIFIER_NAME;
import static com.ecaservice.server.model.entity.EvaluationLog_.CLASSIFIER_INFO;
import static com.ecaservice.server.util.Utils.atEndOfDay;
import static com.ecaservice.server.util.Utils.atStartOfDay;
import static com.ecaservice.server.util.Utils.buildEvaluationResultsDto;
import static com.ecaservice.server.util.Utils.calculateRequestStatusesStatistics;
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
    private final EntityManager entityManager;
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
     * @return evaluation statuses counting statistics
     */
    public RequestStatusStatisticsDto getRequestStatusesStatistics() {
        log.info("Request get evaluations requests statuses statistics");
        List<RequestStatusStatistics> requestStatusStatistics = evaluationLogRepository.getRequestStatusesStatistics();
        var requestStatusStatisticsDto = calculateRequestStatusesStatistics(requestStatusStatistics);
        log.info("Evaluations requests statuses statistics: {}", requestStatusStatisticsDto);
        return calculateRequestStatusesStatistics(requestStatusStatistics);
    }

    /**
     * Calculates classifiers statistics data (distribution diagram by classifier).
     *
     * @param createdDateFrom - created date from
     * @param createdDateTo   - created date to
     * @return classifiers statistics data
     */
    public ChartDto getClassifiersStatisticsData(LocalDate createdDateFrom, LocalDate createdDateTo) {
        log.info("Starting to get classifiers statistics data with created date from [{}] to [{}]",
                createdDateFrom, createdDateTo);
        var criteria = buildClassifiersStatisticsDataCriteria(createdDateFrom, createdDateTo);
        var classifiersStatisticsMap = entityManager.createQuery(criteria)
                .getResultList()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, String.class), tuple -> tuple.get(1, Long.class), (v1, v2) -> v1,
                        TreeMap::new)
                );
        var chartData = populateClassifiersChartData(classifiersStatisticsMap);
        log.info("Classifiers statistics data has been fetched with created date from [{}] to [{}]: {}",
                createdDateFrom, createdDateTo, chartData);
        return chartData;
    }

    private CriteriaQuery<Tuple> buildClassifiersStatisticsDataCriteria(LocalDate createdDateFrom,
                                                                        LocalDate createdDateTo) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteria = builder.createQuery(Tuple.class);
        Root<EvaluationLog> root = criteria.from(EvaluationLog.class);
        List<Predicate> predicates = newArrayList();
        Optional.ofNullable(createdDateFrom).ifPresent(
                value -> predicates.add(builder.greaterThanOrEqualTo(root.get(CREATION_DATE), atStartOfDay(value))));
        Optional.ofNullable(createdDateTo).ifPresent(
                value -> predicates.add(builder.lessThanOrEqualTo(root.get(CREATION_DATE), atEndOfDay(value))));
        Join<EvaluationLog, ClassifierInfo> classifierInfoJoin = root.join(CLASSIFIER_INFO);
        criteria.groupBy(classifierInfoJoin.get(CLASSIFIER_NAME));
        criteria.multiselect(classifierInfoJoin.get(CLASSIFIER_NAME), builder.count(classifierInfoJoin))
                .where(builder.and(predicates.toArray(new Predicate[0])));
        return criteria;
    }

    private ChartDto populateClassifiersChartData(Map<String, Long> classifiersStatisticsMap) {
        var classifiers = filterService.getFilterDictionary(FilterDictionaries.CLASSIFIER_NAME);
        var chartDataItems = classifiers.getValues()
                .stream()
                .map(filterDictionaryValueDto -> {
                    var chartDataDto = new ChartDataDto();
                    chartDataDto.setName(filterDictionaryValueDto.getValue());
                    chartDataDto.setLabel(filterDictionaryValueDto.getLabel());
                    chartDataDto.setCount(
                            classifiersStatisticsMap.getOrDefault(filterDictionaryValueDto.getValue(), 0L));
                    return chartDataDto;
                })
                .collect(Collectors.toList());
        Long total = chartDataItems.stream().mapToLong(ChartDataDto::getCount).sum();
        return ChartDto.builder()
                .dataItems(chartDataItems)
                .total(total)
                .build();
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
