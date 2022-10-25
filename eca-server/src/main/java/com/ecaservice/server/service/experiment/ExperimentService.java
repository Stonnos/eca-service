package com.ecaservice.server.service.experiment;

import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.exception.experiment.ExperimentException;
import com.ecaservice.server.filter.ExperimentFilter;
import com.ecaservice.server.mapping.ExperimentMapper;
import com.ecaservice.server.model.MsgProperties;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.experiment.InitializationParams;
import com.ecaservice.server.model.projections.RequestStatusStatistics;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.PageRequestService;
import com.ecaservice.server.service.evaluation.CalculationExecutorService;
import com.ecaservice.server.service.filter.dictionary.FilterDictionaries;
import com.ecaservice.web.dto.model.ChartDataDto;
import com.ecaservice.web.dto.model.ChartDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import com.ecaservice.web.dto.model.S3ContentResponseDto;
import eca.dataminer.AbstractExperiment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import weka.core.Instances;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.ecaservice.common.web.util.LogHelper.EV_REQUEST_ID;
import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.server.model.entity.AbstractEvaluationEntity_.CREATION_DATE;
import static com.ecaservice.server.model.entity.Experiment_.EXPERIMENT_TYPE;
import static com.ecaservice.server.util.QueryHelper.buildGroupByStatisticsQuery;
import static com.ecaservice.server.util.Utils.calculateRequestStatusesStatistics;

/**
 * Experiment service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentService implements PageRequestService<Experiment> {

    private static final String EXPERIMENT_TRAIN_DATA_PATH_FORMAT = "experiment-train-data-%s.model";
    private static final String EXPERIMENT_PATH_FORMAT = "experiment-%s.model";

    private final ExperimentRepository experimentRepository;
    private final CalculationExecutorService executorService;
    private final ExperimentMapper experimentMapper;
    private final ObjectStorageService objectStorageService;
    private final CrossValidationConfig crossValidationConfig;
    private final ExperimentConfig experimentConfig;
    private final ExperimentProcessorService experimentProcessorService;
    private final EntityManager entityManager;
    private final AppProperties appProperties;
    private final FilterService filterService;

    /**
     * Creates experiment request.
     *
     * @param experimentRequest - experiment request {@link ExperimentRequest}
     * @param msgProperties     - message properties
     * @return created experiment entity
     */
    public Experiment createExperiment(ExperimentRequest experimentRequest, MsgProperties msgProperties) {
        String requestId = UUID.randomUUID().toString();
        putMdc(TX_ID, requestId);
        putMdc(EV_REQUEST_ID, requestId);
        log.info("Received experiment [{}] request for data '{}', evaluation method [{}], email '{}'",
                experimentRequest.getExperimentType(), experimentRequest.getData().relationName(),
                experimentRequest.getEvaluationMethod(), experimentRequest.getEmail());
        Assert.notNull(msgProperties, "Expected not null message properties");
        Assert.notNull(msgProperties.getChannel(), "Expected not null channel");
        try {
            Experiment experiment = experimentMapper.map(experimentRequest, crossValidationConfig);
            setMessageProperties(experiment, msgProperties);
            experiment.setRequestStatus(RequestStatus.NEW);
            experiment.setRequestId(requestId);
            String objectPath = String.format(EXPERIMENT_TRAIN_DATA_PATH_FORMAT, experiment.getRequestId());
            objectStorageService.uploadObject(experimentRequest.getData(), objectPath);
            experiment.setTrainingDataPath(objectPath);
            experiment.setCreationDate(LocalDateTime.now());
            return experimentRepository.save(experiment);
        } catch (Exception ex) {
            log.error("There was an error while create experiment request [{}]: {}", requestId, ex.getMessage());
            throw new ExperimentException(ex.getMessage());
        }
    }

    /**
     * Processes new experiment. Experiments results are saved into file after processing.
     *
     * @param experiment - experiment to process
     * @return experiment history
     */
    public AbstractExperiment<?> processExperiment(final Experiment experiment) {
        log.info("Starting to built experiment [{}].", experiment.getRequestId());
        try {
            if (StringUtils.isEmpty(experiment.getTrainingDataPath())) {
                throw new ExperimentException(String.format("Training data path is not specified for experiment [%s]!",
                        experiment.getRequestId()));
            }

            StopWatch stopWatch =
                    new StopWatch(String.format("Stop watching for experiment [%s]", experiment.getRequestId()));
            stopWatch.start(String.format("Loading data for experiment [%s]", experiment.getRequestId()));
            Instances data = objectStorageService.getObject(experiment.getTrainingDataPath(), Instances.class);
            data.setClassIndex(experiment.getClassIndex());
            stopWatch.stop();

            final InitializationParams initializationParams =
                    new InitializationParams(data, experiment.getEvaluationMethod());
            stopWatch.start(String.format("Experiment [%s] processing", experiment.getRequestId()));
            Callable<AbstractExperiment<?>> callable = () ->
                    experimentProcessorService.processExperimentHistory(experiment, initializationParams);
            AbstractExperiment<?> abstractExperiment =
                    executorService.execute(callable, experimentConfig.getTimeout(), TimeUnit.HOURS);
            stopWatch.stop();

            stopWatch.start(String.format("Experiment [%s] saving", experiment.getRequestId()));
            String experimentPath = String.format(EXPERIMENT_PATH_FORMAT, experiment.getRequestId());
            objectStorageService.uploadObject(abstractExperiment, experimentPath);
            stopWatch.stop();

            experiment.setExperimentPath(experimentPath);
            String experimentDownloadUrl = getExperimentDownloadPresignedUrl(experimentPath);
            experiment.setExperimentDownloadUrl(experimentDownloadUrl);
            experiment.setRequestStatus(RequestStatus.FINISHED);
            log.info("Experiment [{}] has been successfully built!", experiment.getRequestId());
            log.info(stopWatch.prettyPrint());
            return abstractExperiment;
        } catch (TimeoutException ex) {
            log.warn("There was a timeout while experiment [{}] built.", experiment.getRequestId());
            experiment.setRequestStatus(RequestStatus.TIMEOUT);
        } catch (Exception ex) {
            log.error("There was an error while experiment [{}] built: {}", experiment.getRequestId(), ex);
            experiment.setRequestStatus(RequestStatus.ERROR);
            experiment.setErrorMessage(ex.getMessage());
        } finally {
            experiment.setEndDate(LocalDateTime.now());
            experimentRepository.save(experiment);
        }
        return null;
    }

    /**
     * Removes experiment model file from disk.
     *
     * @param experiment - experiment entity
     */
    @Transactional
    public void removeExperimentModel(Experiment experiment) {
        log.info("Starting to remove experiment [{}] model file", experiment.getRequestId());
        String experimentPath = experiment.getExperimentPath();
        experiment.setExperimentPath(null);
        experiment.setExperimentDownloadUrl(null);
        experiment.setDeletedDate(LocalDateTime.now());
        experimentRepository.save(experiment);
        objectStorageService.removeObject(experimentPath);
        log.info("Experiment [{}] model file has been deleted", experiment.getRequestId());
    }

    /**
     * Removes experiment training data file from disk.
     *
     * @param experiment - experiment entity
     */
    @Transactional
    public void removeExperimentTrainingData(Experiment experiment) {
        log.info("Starting to remove experiment [{}] training data file", experiment.getRequestId());
        String trainingDataPath = experiment.getTrainingDataPath();
        experiment.setTrainingDataPath(null);
        experimentRepository.save(experiment);
        objectStorageService.removeObject(trainingDataPath);
        log.info("Experiment [{}] training data file has been deleted", experiment.getRequestId());
    }

    @Override
    public Page<Experiment> getNextPage(PageRequestDto pageRequestDto) {
        log.info("Gets experiments next page: {}", pageRequestDto);
        Sort sort = buildSort(pageRequestDto.getSortField(), CREATION_DATE, pageRequestDto.isAscending());
        List<String> globalFilterFields = filterService.getGlobalFilterFields(FilterTemplateType.EXPERIMENT.name());
        ExperimentFilter filter =
                new ExperimentFilter(pageRequestDto.getSearchQuery(), globalFilterFields, pageRequestDto.getFilters());
        int pageSize = Integer.min(pageRequestDto.getSize(), appProperties.getMaxPageSize());
        var experimentsPage =
                experimentRepository.findAll(filter, PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
        log.info("Experiments page [{} of {}] with size [{}] has been fetched for page request [{}]",
                experimentsPage.getNumber(), experimentsPage.getTotalPages(), experimentsPage.getNumberOfElements(),
                pageRequestDto);
        return experimentsPage;
    }

    /**
     * Gets experiment by id.
     *
     * @param id - experiment id
     * @return experiment entity
     */
    public Experiment getById(Long id) {
        return experimentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Experiment.class, id));
    }

    /**
     * Calculates experiment statuses counting statistics.
     *
     * @return requests status statistics dto
     */
    public RequestStatusStatisticsDto getRequestStatusesStatistics() {
        log.info("Request get experiments statuses statistics");
        List<RequestStatusStatistics> requestStatusStatistics = experimentRepository.getRequestStatusesStatistics();
        var requestStatusStatisticsDto = calculateRequestStatusesStatistics(requestStatusStatistics);
        log.info("Experiments statuses statistics: {}", requestStatusStatisticsDto);
        return requestStatusStatisticsDto;
    }

    /**
     * Calculates experiments statistics data (distribution diagram by experiment type).
     *
     * @param createdDateFrom - experiment created date from
     * @param createdDateTo   - experiment created date to
     * @return experiments statistics
     */
    public ChartDto getExperimentsStatistics(LocalDate createdDateFrom, LocalDate createdDateTo) {
        log.info("Starting to get experiments statistics data with created date from [{}] to [{}]",
                createdDateFrom, createdDateTo);
        CriteriaQuery<Tuple> criteria = buildExperimentStatisticsDataCriteria(createdDateFrom, createdDateTo);
        var experimentStatisticsMap = entityManager.createQuery(criteria).getResultList()
                .stream()
                .collect(Collectors.toMap(tuple -> tuple.get(0, ExperimentType.class).name(),
                        tuple -> tuple.get(1, Long.class), (v1, v2) -> v1, TreeMap::new));
        var chartData = populateExperimentsChartData(experimentStatisticsMap);
        log.info("Experiments statistics data has been fetched with created date from [{}] to [{}]: {}",
                createdDateFrom, createdDateTo, chartData);
        return chartData;
    }

    private CriteriaQuery<Tuple> buildExperimentStatisticsDataCriteria(LocalDate createdDateFrom,
                                                                       LocalDate createdDateTo) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        return buildGroupByStatisticsQuery(builder, Experiment.class, root -> root.get(EXPERIMENT_TYPE), CREATION_DATE,
                createdDateFrom, createdDateTo);
    }

    private ChartDto populateExperimentsChartData(Map<String, Long> statisticsMap) {
        var classifiers = filterService.getFilterDictionary(FilterDictionaries.EXPERIMENT_TYPE);
        var chartDataItems = classifiers.getValues()
                .stream()
                .map(filterDictionaryValueDto -> {
                    var chartDataDto = new ChartDataDto();
                    chartDataDto.setName(filterDictionaryValueDto.getValue());
                    chartDataDto.setLabel(filterDictionaryValueDto.getLabel());
                    chartDataDto.setCount(
                            statisticsMap.getOrDefault(filterDictionaryValueDto.getValue(), 0L));
                    return chartDataDto;
                })
                .collect(Collectors.toList());
        Long total = chartDataItems.stream().mapToLong(ChartDataDto::getCount).sum();
        return ChartDto.builder()
                .dataItems(chartDataItems)
                .total(total)
                .build();
    }

    /**
     * Gets experiment results content url.
     *
     * @param id - experiment id
     * @return s3 content response dto
     */
    public S3ContentResponseDto getExperimentResultsContentUrl(Long id) {
        log.info("Starting to get experiment [{}] results content url", id);
        var experiment = getById(id);
        String contentUrl = objectStorageService.getObjectPresignedProxyUrl(
                GetPresignedUrlObject.builder()
                        .objectPath(experiment.getExperimentPath())
                        .expirationTime(experimentConfig.getShortLifeUrlExpirationMinutes())
                        .expirationTimeUnit(TimeUnit.MINUTES)
                        .build()
        );
        log.info("Experiment [{}] results content url has been fetched", id);
        return S3ContentResponseDto.builder()
                .contentUrl(contentUrl)
                .build();
    }

    private String getExperimentDownloadPresignedUrl(String experimentPath) {
        return objectStorageService.getObjectPresignedProxyUrl(
                GetPresignedUrlObject.builder()
                        .objectPath(experimentPath)
                        .expirationTime(experimentConfig.getExperimentDownloadUrlExpirationDays())
                        .expirationTimeUnit(TimeUnit.DAYS)
                        .build()
        );
    }

    private void setMessageProperties(Experiment experiment, MsgProperties msgProperties) {
        experiment.setChannel(msgProperties.getChannel());
        if (Channel.QUEUE.equals(experiment.getChannel())) {
            experiment.setReplyTo(msgProperties.getReplyTo());
            experiment.setCorrelationId(msgProperties.getCorrelationId());
        }
    }
}
