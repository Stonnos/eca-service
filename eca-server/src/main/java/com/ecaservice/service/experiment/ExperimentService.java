package com.ecaservice.service.experiment;

import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.config.CommonConfig;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.exception.experiment.ExperimentException;
import com.ecaservice.exception.experiment.ResultsNotFoundException;
import com.ecaservice.filter.ExperimentFilter;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.model.entity.AppInstanceEntity;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.InitializationParams;
import com.ecaservice.model.projections.RequestStatusStatistics;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.AppInstanceService;
import com.ecaservice.service.PageRequestService;
import com.ecaservice.service.evaluation.CalculationExecutorService;
import com.ecaservice.service.filter.FilterService;
import com.ecaservice.util.SortUtils;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.converters.model.ExperimentHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import weka.core.Instances;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ecaservice.common.web.util.LogHelper.EV_REQUEST_ID;
import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.model.entity.AbstractEvaluationEntity_.CREATION_DATE;
import static com.ecaservice.model.entity.Experiment_.EXPERIMENT_TYPE;
import static com.ecaservice.util.ExperimentUtils.generateToken;
import static com.ecaservice.util.ExperimentUtils.getExperimentFile;
import static com.ecaservice.util.Utils.atEndOfDay;
import static com.ecaservice.util.Utils.atStartOfDay;
import static com.ecaservice.util.Utils.existsFile;
import static com.ecaservice.util.Utils.toRequestStatusStatisticsMap;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Experiment service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentService implements PageRequestService<Experiment> {

    private final ExperimentRepository experimentRepository;
    private final CalculationExecutorService executorService;
    private final ExperimentMapper experimentMapper;
    private final DataService dataService;
    private final CrossValidationConfig crossValidationConfig;
    private final ExperimentConfig experimentConfig;
    private final ExperimentProcessorService experimentProcessorService;
    private final EntityManager entityManager;
    private final CommonConfig commonConfig;
    private final FilterService filterService;
    private final AppInstanceService appInstanceService;

    /**
     * Creates experiment request.
     *
     * @param experimentRequest - experiment request {@link ExperimentRequest}
     * @return created experiment entity
     */
    public Experiment createExperiment(ExperimentRequest experimentRequest) {
        String requestId = UUID.randomUUID().toString();
        putMdc(TX_ID, requestId);
        putMdc(EV_REQUEST_ID, requestId);
        log.info("Received experiment [{}] request for data '{}', evaluation method [{}], email '{}'",
                experimentRequest.getExperimentType(), experimentRequest.getData().relationName(),
                experimentRequest.getEvaluationMethod(), experimentRequest.getEmail());
        try {
            AppInstanceEntity appInstanceEntity = appInstanceService.getAppInstanceEntity();
            Experiment experiment = experimentMapper.map(experimentRequest, crossValidationConfig);
            experiment.setRequestStatus(RequestStatus.NEW);
            experiment.setRequestId(requestId);
            experiment.setAppInstanceEntity(appInstanceEntity);
            File dataFile = new File(experimentConfig.getData().getStoragePath(),
                    String.format(experimentConfig.getData().getFileFormat(), experiment.getRequestId()));
            dataService.save(dataFile, experimentRequest.getData());
            experiment.setTrainingDataAbsolutePath(dataFile.getAbsolutePath());
            experiment.setCreationDate(LocalDateTime.now());
            return experimentRepository.save(experiment);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new ExperimentException(ex.getMessage());
        }
    }

    /**
     * Processes new experiment. Experiments results are saved into file after processing.
     *
     * @param experiment - experiment to process
     * @return experiment history
     */
    public ExperimentHistory processExperiment(final Experiment experiment) {
        log.info("Starting to built experiment [{}].", experiment.getRequestId());
        try {
            if (StringUtils.isEmpty(experiment.getTrainingDataAbsolutePath())) {
                throw new ExperimentException(String.format("Training data path is not specified for experiment [%s]!",
                        experiment.getRequestId()));
            }

            StopWatch stopWatch =
                    new StopWatch(String.format("Stop watching for experiment [%s]", experiment.getRequestId()));
            stopWatch.start(String.format("Loading data for experiment [%s]", experiment.getRequestId()));
            Instances data = dataService.load(new File(experiment.getTrainingDataAbsolutePath()));
            data.setClassIndex(experiment.getClassIndex());
            stopWatch.stop();

            final InitializationParams initializationParams =
                    new InitializationParams(data, experiment.getEvaluationMethod());
            stopWatch.start(String.format("Experiment [%s] processing", experiment.getRequestId()));
            Callable<ExperimentHistory> callable = () ->
                    experimentProcessorService.processExperimentHistory(experiment, initializationParams);
            ExperimentHistory experimentHistory =
                    executorService.execute(callable, experimentConfig.getTimeout(), TimeUnit.HOURS);
            stopWatch.stop();

            stopWatch.start(String.format("Experiment [%s] saving", experiment.getRequestId()));
            File experimentFile = new File(experimentConfig.getStoragePath(),
                    String.format(experimentConfig.getFileFormat(), experiment.getRequestId()));
            dataService.saveExperimentHistory(experimentFile, experimentHistory);
            stopWatch.stop();

            experiment.setExperimentAbsolutePath(experimentFile.getAbsolutePath());
            experiment.setToken(generateToken(experiment));
            experiment.setRequestStatus(RequestStatus.FINISHED);
            log.info("Experiment [{}] has been successfully finished!", experiment.getRequestId());
            log.info(stopWatch.prettyPrint());
            return experimentHistory;
        } catch (TimeoutException ex) {
            log.warn("There was a timeout for experiment [{}].", experiment.getRequestId());
            experiment.setRequestStatus(RequestStatus.TIMEOUT);
        } catch (Exception ex) {
            log.error("There was an error occurred for experiment [{}]: {}", experiment.getRequestId(), ex);
            experiment.setRequestStatus(RequestStatus.ERROR);
            experiment.setErrorMessage(ex.getMessage());
        } finally {
            experiment.setEndDate(LocalDateTime.now());
            experimentRepository.save(experiment);
        }
        return null;
    }

    /**
     * Gets experiment history.
     *
     * @param experiment - experiment entity
     * @return experiment history
     */
    public ExperimentHistory getExperimentHistory(Experiment experiment) {
        File experimentFile = getExperimentFile(experiment, Experiment::getExperimentAbsolutePath);
        if (!existsFile(experimentFile)) {
            throw new ResultsNotFoundException(
                    String.format("Experiment results file not found for experiment [%s]!", experiment.getRequestId()));
        }
        try {
            return dataService.loadExperimentHistory(experimentFile);
        } catch (Exception ex) {
            throw new ExperimentException(ex.getMessage());
        }
    }

    /**
     * Removes experiments data files from disk.
     *
     * @param experiment - experiment object
     */
    public void removeExperimentData(Experiment experiment) {
        boolean trainingDataDeleted = removeExperimentFile(experiment, Experiment::getTrainingDataAbsolutePath,
                exp -> exp.setTrainingDataAbsolutePath(null));
        if (trainingDataDeleted) {
            boolean experimentResultsDeleted = removeExperimentFile(experiment, Experiment::getExperimentAbsolutePath,
                    exp -> exp.setExperimentAbsolutePath(null));
            if (experimentResultsDeleted) {
                experiment.setDeletedDate(LocalDateTime.now());
            }
            experimentRepository.save(experiment);
        }
    }

    @Override
    public Page<Experiment> getNextPage(PageRequestDto pageRequestDto) {
        Sort sort = SortUtils.buildSort(pageRequestDto.getSortField(), CREATION_DATE, pageRequestDto.isAscending());
        List<String> globalFilterFields = filterService.getGlobalFilterFields(FilterTemplateType.EXPERIMENT);
        ExperimentFilter filter =
                new ExperimentFilter(pageRequestDto.getSearchQuery(), globalFilterFields, pageRequestDto.getFilters());
        int pageSize = Integer.min(pageRequestDto.getSize(), commonConfig.getMaxPageSize());
        return experimentRepository.findAll(filter, PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
    }

    /**
     * Calculates experiment statuses counting statistics.
     *
     * @return requests status counting statistics list
     */
    public Map<RequestStatus, Long> getRequestStatusesStatistics() {
        List<RequestStatusStatistics> requestStatusStatistics = experimentRepository.getRequestStatusesStatistics();
        return toRequestStatusStatisticsMap(requestStatusStatistics);
    }

    /**
     * Calculates experiments types counting statistics.
     *
     * @param createdDateFrom - experiment created date from
     * @param createdDateTo   - experiment created date to
     * @return experiments types counting statistics
     */
    public Map<ExperimentType, Long> getExperimentTypesStatistics(LocalDate createdDateFrom, LocalDate createdDateTo) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteria = builder.createQuery(Tuple.class);
        Root<Experiment> root = criteria.from(Experiment.class);
        List<Predicate> predicates = newArrayList();
        Optional.ofNullable(createdDateFrom).ifPresent(value -> predicates.add(
                builder.greaterThanOrEqualTo(root.get(CREATION_DATE), atStartOfDay(value))));
        Optional.ofNullable(createdDateTo).ifPresent(value -> predicates.add(
                builder.lessThanOrEqualTo(root.get(CREATION_DATE), atEndOfDay(value))));
        criteria.groupBy(root.get(EXPERIMENT_TYPE));
        criteria.multiselect(root.get(EXPERIMENT_TYPE), builder.count(root)).where(
                builder.and(predicates.toArray(new Predicate[0])));
        Map<ExperimentType, Long> experimentTypesMap =
                entityManager.createQuery(criteria).getResultList().stream().collect(
                        Collectors.toMap(tuple -> tuple.get(0, ExperimentType.class),
                                tuple -> tuple.get(1, Long.class), (v1, v2) -> v1, TreeMap::new));
        Arrays.stream(ExperimentType.values()).filter(
                requestStatus -> !experimentTypesMap.containsKey(requestStatus)).forEach(
                requestStatus -> experimentTypesMap.put(requestStatus, 0L));
        return experimentTypesMap;
    }

    /**
     * Removes experiment associated file.
     *
     * @param experiment       - experiment entity
     * @param filePathFunction - file path function
     * @param callback         - callback
     * @return {@code true} if file has been successfully removed or file path is empty
     */
    private boolean removeExperimentFile(Experiment experiment, Function<Experiment, String> filePathFunction,
                                         Consumer<Experiment> callback) {
        String filePath = filePathFunction.apply(experiment);
        if (!StringUtils.isEmpty(filePath)) {
            boolean deleted = dataService.delete(new File(filePath));
            if (deleted) {
                callback.accept(experiment);
            }
            return deleted;
        }
        return true;
    }
}
