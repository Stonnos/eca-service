package com.ecaservice.service.experiment;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.exception.ExperimentException;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.model.experiment.InitializationParams;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.evaluation.CalculationExecutorService;
import eca.converters.model.ExperimentHistory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import weka.core.Instances;

import javax.inject.Inject;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Experiment service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ExperimentService {

    private final ExperimentRepository experimentRepository;
    private final CalculationExecutorService executorService;
    private final ExperimentMapper experimentMapper;
    private final DataService dataService;
    private final ExperimentConfig experimentConfig;
    private final ExperimentProcessorService experimentProcessorService;

    /**
     * Constructor with dependency spring injection.
     *
     * @param experimentRepository       - experiment repository bean
     * @param executorService            - calculation executor service bean
     * @param experimentMapper           - experiment mapper bean
     * @param dataService                - data service bean
     * @param experimentConfig           - experiment config bean
     * @param experimentProcessorService - experiment processor service bean
     */
    @Inject
    public ExperimentService(ExperimentRepository experimentRepository,
                             CalculationExecutorService executorService,
                             ExperimentMapper experimentMapper,
                             DataService dataService,
                             ExperimentConfig experimentConfig,
                             ExperimentProcessorService experimentProcessorService) {
        this.experimentRepository = experimentRepository;
        this.executorService = executorService;
        this.experimentMapper = experimentMapper;
        this.dataService = dataService;
        this.experimentConfig = experimentConfig;
        this.experimentProcessorService = experimentProcessorService;
    }

    /**
     * Creates experiment request.
     *
     * @param experimentRequest - experiment request {@link ExperimentRequest}
     * @return created experiment entity
     */
    public Experiment createExperiment(ExperimentRequest experimentRequest) {
        Assert.notNull(experimentRequest, "Experiment request is not specified!");
        Experiment experiment = experimentMapper.map(experimentRequest);
        try {
            experiment.setExperimentStatus(ExperimentStatus.NEW);
            experiment.setUuid(UUID.randomUUID().toString());
            experiment.setCreationDate(LocalDateTime.now());
            File dataFile = new File(experimentConfig.getData().getStoragePath(),
                    String.format(experimentConfig.getData().getFileFormat(), System.currentTimeMillis()));
            dataService.save(dataFile, experimentRequest.getData());
            experiment.setTrainingDataAbsolutePath(dataFile.getAbsolutePath());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            experiment.setExperimentStatus(ExperimentStatus.ERROR);
            experiment.setErrorMessage(ex.getMessage());
        } finally {
            experimentRepository.save(experiment);
        }
        return experiment;
    }

    /**
     * Processes new experiment. Experiments results are saved into file after processing.
     *
     * @param experiment - experiment to process
     * @return experiment history
     */
    public ExperimentHistory processExperiment(final Experiment experiment) {
        log.info("Starting to built experiment {}.", experiment.getId());
        experiment.setStartDate(LocalDateTime.now());
        experimentRepository.save(experiment);
        try {
            if (StringUtils.isEmpty(experiment.getTrainingDataAbsolutePath())) {
                throw new ExperimentException("Training data path is not specified!");
            }

            StopWatch stopWatch = new StopWatch(String.format("Stop watching for experiment %d", experiment.getId()));
            stopWatch.start(String.format("Loading data for experiment %d", experiment.getId()));
            Instances data = dataService.load(new File(experiment.getTrainingDataAbsolutePath()));
            stopWatch.stop();

            final InitializationParams initializationParams =
                    new InitializationParams(data, experiment.getEvaluationMethod());
            stopWatch.start(String.format("Experiment %d processing", experiment.getId()));
            Callable<ExperimentHistory> callable = () ->
                    experimentProcessorService.processExperimentHistory(experiment, initializationParams);
            ExperimentHistory experimentHistory =
                    executorService.execute(callable, experimentConfig.getTimeout(), TimeUnit.HOURS);
            stopWatch.stop();

            stopWatch.start(String.format("Experiment %d saving", experiment.getId()));
            File experimentFile = new File(experimentConfig.getStoragePath(),
                    String.format(experimentConfig.getFileFormat(), System.currentTimeMillis()));
            dataService.saveExperimentHistory(experimentFile, experimentHistory);
            stopWatch.stop();

            experiment.setExperimentAbsolutePath(experimentFile.getAbsolutePath());
            experiment.setExperimentStatus(ExperimentStatus.FINISHED);
            log.info("Experiment {} has been successfully finished!", experiment.getId());
            log.info(stopWatch.prettyPrint());
            return experimentHistory;
        } catch (TimeoutException ex) {
            log.warn("There was a timeout for experiment with id = {}.", experiment.getId());
            experiment.setExperimentStatus(ExperimentStatus.TIMEOUT);
        } catch (Exception ex) {
            log.error("There was an error occurred for experiment with id = {}: {}", experiment.getId(), ex);
            experiment.setExperimentStatus(ExperimentStatus.ERROR);
            experiment.setErrorMessage(ex.getMessage());
        } finally {
            experiment.setEndDate(LocalDateTime.now());
            experimentRepository.save(experiment);
        }
        return null;
    }

    /**
     * Finds experiment file by uuid.
     *
     * @param uuid - experiment uuid
     * @return experiment file object
     */
    public File findExperimentFileByUuid(String uuid) {
        Experiment experiment = experimentRepository.findByUuidAndExperimentStatusIn(uuid,
                Collections.singletonList(ExperimentStatus.FINISHED));
        if (Optional.ofNullable(experiment).map(Experiment::getExperimentAbsolutePath).isPresent()) {
            return new File(experiment.getExperimentAbsolutePath());
        } else {
            return null;
        }
    }

    /**
     * Removes experiments data files from disk.
     *
     * @param experiment - experiment object
     */
    public void removeExperimentData(Experiment experiment) {
        if (Optional.ofNullable(experiment).map(Experiment::getTrainingDataAbsolutePath).isPresent()) {
            dataService.delete(new File(experiment.getTrainingDataAbsolutePath()));
            experiment.setTrainingDataAbsolutePath(null);
        }
        if (Optional.ofNullable(experiment).map(Experiment::getExperimentAbsolutePath).isPresent()) {
            dataService.delete(new File(experiment.getExperimentAbsolutePath()));
            experiment.setExperimentAbsolutePath(null);
        }
        experiment.setDeletedDate(LocalDateTime.now());
        experimentRepository.save(experiment);
    }
}
