package com.ecaservice.server.service.evaluation;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.filter.query.FilterQueryExecutor;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.filter.specification.FilterFieldCustomizer;
import com.ecaservice.core.filter.specification.UuidFilterFieldCustomizer;
import com.ecaservice.core.filter.validation.annotations.ValidPageRequest;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.bpm.model.EvaluationLogModel;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.config.ClassifiersProperties;
import com.ecaservice.server.filter.EvaluationLogFilter;
import com.ecaservice.server.filter.customize.ClassifierNameFilterFieldCustomizer;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.EvaluationLog_;
import com.ecaservice.server.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.projections.RequestStatusStatistics;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.EvaluationResultsRequestEntityRepository;
import com.ecaservice.server.service.classifiers.ClassifierOptionsInfoProcessor;
import com.ecaservice.server.service.ers.ErsService;
import com.ecaservice.server.service.filter.dictionary.FilterDictionaries;
import com.ecaservice.web.dto.model.ChartDto;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import com.ecaservice.web.dto.model.S3ContentResponseDto;
import io.micrometer.tracing.annotation.NewSpan;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.server.model.entity.AbstractEvaluationEntity_.CREATION_DATE;
import static com.ecaservice.server.model.entity.EvaluationLog_.CLASSIFIER_NAME;
import static com.ecaservice.server.model.entity.FilterTemplateType.EVALUATION_LOG;
import static com.ecaservice.server.util.QueryHelper.buildGroupByStatisticsQuery;
import static com.ecaservice.server.util.StatisticsHelper.calculateChartData;
import static com.ecaservice.server.util.StatisticsHelper.calculateRequestStatusesStatistics;
import static com.ecaservice.server.util.Utils.buildEvaluationResultsDto;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Evaluation log data service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class EvaluationLogDataService {

    private final ClassifiersProperties classifiersProperties;
    private final AppProperties appProperties;
    private final FilterTemplateService filterTemplateService;
    private final EvaluationLogMapper evaluationLogMapper;
    private final ClassifierOptionsInfoProcessor classifierOptionsInfoProcessor;
    private final ErsService ersService;
    private final EntityManager entityManager;
    private final ObjectStorageService objectStorageService;
    private final EvaluationLogRepository evaluationLogRepository;
    private final EvaluationResultsRequestEntityRepository evaluationResultsRequestEntityRepository;
    private final EvaluationLogCountQueryExecutor countQueryExecutor;

    private final List<FilterFieldCustomizer> globalFilterFieldCustomizers = newArrayList();

    /**
     * Initialization method.
     */
    @PostConstruct
    public void initialize() {
        globalFilterFieldCustomizers.add(new ClassifierNameFilterFieldCustomizer(filterTemplateService));
        globalFilterFieldCustomizers.add(new UuidFilterFieldCustomizer(EvaluationLog_.REQUEST_ID));
    }

    /**
     * Gets evaluation logs page.
     *
     * @param pageRequestDto - page request dto
     * @return evaluation logs page
     */
    public Page<EvaluationLog> getNextPage(
            @ValidPageRequest(filterTemplateName = EVALUATION_LOG) PageRequestDto pageRequestDto) {
        log.info("Gets evaluation logs next page: {}", pageRequestDto);
        Sort sort = buildSort(pageRequestDto.getSortFields(), CREATION_DATE, true);
        List<String> globalFilterFields = filterTemplateService.getGlobalFilterFields(EVALUATION_LOG);
        var filter = new EvaluationLogFilter(pageRequestDto.getSearchQuery(), globalFilterFields,
                pageRequestDto.getFilters());
        filter.setGlobalFilterFieldsCustomizers(globalFilterFieldCustomizers);
        var pageRequest = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort);
        FilterQueryExecutor filterQueryExecutor = new FilterQueryExecutor(entityManager);
        Page<EvaluationLog> evaluationLogsPage =
                filterQueryExecutor.executePageQuery(pageRequestDto, filter, pageRequest, countQueryExecutor);
        log.info("Evaluation logs page [{} of {}] with size [{}] has been fetched for page request [{}]",
                evaluationLogsPage.getNumber(), evaluationLogsPage.getTotalPages(),
                evaluationLogsPage.getNumberOfElements(), pageRequestDto);
        return evaluationLogsPage;
    }

    /**
     * Gets evaluation logs page.
     *
     * @param pageRequestDto - page request dto
     * @return evaluation logs page
     */
    public PageDto<EvaluationLogDto> getEvaluationLogsPage(
            @ValidPageRequest(filterTemplateName = EVALUATION_LOG) PageRequestDto pageRequestDto) {
        var evaluationLogsPage = getNextPage(pageRequestDto);
        List<EvaluationLogDto> evaluationLogDtoList = evaluationLogsPage.getContent()
                .stream()
                .map(evaluationLog -> {
                    var evaluationLogDto = evaluationLogMapper.map(evaluationLog);
                    var classifierInfoDto =
                            classifierOptionsInfoProcessor.processClassifierInfo(evaluationLog.getClassifierOptions());
                    evaluationLogDto.setClassifierInfo(classifierInfoDto);
                    return evaluationLogDto;
                })
                .collect(Collectors.toList());
        long totalElements = Long.min(evaluationLogsPage.getTotalElements(),
                pageRequestDto.getSize() * appProperties.getMaxPagesNum());
        return PageDto.of(evaluationLogDtoList, pageRequestDto.getPage(), totalElements);
    }

    /**
     * Gets evaluation log report.
     *
     * @param evaluationLog - evaluation log entity
     * @return evaluation log details dto
     */
    public EvaluationLogDetailsDto getEvaluationLogDetails(EvaluationLog evaluationLog) {
        var evaluationLogDetailsDto = evaluationLogMapper.mapDetails(evaluationLog);
        var classifierInfoDto =
                classifierOptionsInfoProcessor.processClassifierInfo(evaluationLog.getClassifierOptions());
        evaluationLogDetailsDto.setClassifierInfo(classifierInfoDto);
        evaluationLogDetailsDto.setEvaluationResultsDto(getEvaluationResults(evaluationLog));
        return evaluationLogDetailsDto;
    }

    /**
     * Calculates evaluation statuses counting statistics.
     *
     * @return evaluation statuses counting statistics
     */
    @NewSpan
    public RequestStatusStatisticsDto getRequestStatusesStatistics() {
        log.info("Request get evaluations requests statuses statistics");
        List<RequestStatusStatistics> requestStatusStatistics = evaluationLogRepository.getRequestStatusesStatistics();
        var requestStatusStatisticsDto = calculateRequestStatusesStatistics(requestStatusStatistics);
        log.info("Evaluations requests statuses statistics: {}", requestStatusStatisticsDto);
        return requestStatusStatisticsDto;
    }

    /**
     * Calculates classifiers statistics data (distribution diagram by classifier).
     *
     * @param createdDateFrom - created date from
     * @param createdDateTo   - created date to
     * @return classifiers statistics data
     */
    @NewSpan
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

    /**
     * Gets evaluation log by id.
     *
     * @param id - evaluation log id
     * @return evaluation log entity
     */
    public EvaluationLog getById(Long id) {
        return evaluationLogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EvaluationLog.class, id));
    }

    /**
     * Gets evaluation log model.
     *
     * @param id - evaluation log id
     * @return evaluation log model
     */
    public EvaluationLogModel getEvaluationLogModel(Long id) {
        var evaluationLog = getById(id);
        return evaluationLogMapper.mapToModel(evaluationLog);
    }

    /**
     * Gets classifier model content url.
     *
     * @param id - experiment id
     * @return s3 content response dto
     */
    public S3ContentResponseDto getModelContentUrl(Long id) {
        log.info("Starting to get classifier model [{}] results content url", id);
        var evaluationLog = getById(id);
        String contentUrl = objectStorageService.getObjectPresignedProxyUrl(
                GetPresignedUrlObject.builder()
                        .objectPath(evaluationLog.getModelPath())
                        .expirationTime(appProperties.getShortLifeUrlExpirationMinutes())
                        .expirationTimeUnit(TimeUnit.MINUTES)
                        .build()
        );
        log.info("Classifier model [{}] content url has been fetched", id);
        return S3ContentResponseDto.builder()
                .contentUrl(contentUrl)
                .build();
    }

    /**
     * Removes classifier model file from object storage.
     *
     * @param evaluationLog - evaluation log entity
     */
    @NewSpan
    public void removeModel(EvaluationLog evaluationLog) {
        try {
            log.info("Starting to remove classifier [{}] model file", evaluationLog.getRequestId());
            evaluationLogRepository.save(evaluationLog);
            String modelPath = evaluationLog.getModelPath();
            objectStorageService.removeObject(modelPath);
            evaluationLog.setModelPath(null);
            evaluationLog.setDeletedDate(LocalDateTime.now());
            evaluationLogRepository.save(evaluationLog);
            log.info("Classifier [{}] model file has been deleted", evaluationLog.getRequestId());
        } catch (Exception ex) {
            log.error("There was an error while remove classifier [{}] model file: {}", evaluationLog.getRequestId(),
                    ex.getMessage());
            evaluationLog.setRetryAt(LocalDateTime.now().plusSeconds(classifiersProperties.getRetryIntervalSeconds()));
            evaluationLogRepository.save(evaluationLog);
        }
    }

    private CriteriaQuery<Tuple> buildClassifiersStatisticsDataCriteria(LocalDate createdDateFrom,
                                                                        LocalDate createdDateTo) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        return buildGroupByStatisticsQuery(builder, EvaluationLog.class, root -> root.get(CLASSIFIER_NAME),
                CREATION_DATE, createdDateFrom, createdDateTo);
    }

    private ChartDto populateClassifiersChartData(Map<String, Long> classifiersStatisticsMap) {
        var classifiers = filterTemplateService.getFilterDictionary(FilterDictionaries.CLASSIFIER_NAME);
        return calculateChartData(classifiers, classifiersStatisticsMap);
    }

    private EvaluationResultsDto getEvaluationResults(EvaluationLog evaluationLog) {
        EvaluationResultsStatus evaluationResultsStatus;
        if (!RequestStatus.FINISHED.equals(evaluationLog.getRequestStatus())) {
            if (RequestStatus.NEW.equals(evaluationLog.getRequestStatus()) ||
                    RequestStatus.IN_PROGRESS.equals(evaluationLog.getRequestStatus())) {
                evaluationResultsStatus = EvaluationResultsStatus.EVALUATION_IN_PROGRESS;
            } else {
                evaluationResultsStatus = EvaluationResultsStatus.EVALUATION_ERROR;
            }
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
