package com.ecaservice.service.experiment;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.mapping.OrikaBeanMapper;
import com.ecaservice.model.EvaluationStatus;
import com.ecaservice.model.ExperimentRequest;
import com.ecaservice.model.ExperimentRequestResult;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.CalculationExecutorService;
import eca.converters.ModelConverter;
import eca.converters.model.ExperimentHistory;
import eca.dataminer.AbstractExperiment;
import eca.dataminer.IterativeExperiment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ExperimentService {

    private final ExperimentRepository experimentRepository;
    private final CalculationExecutorService executorService;
    private final OrikaBeanMapper mapper;
    private final DataService dataService;
    private final ExperimentConfig experimentConfig;
    private final ExperimentInitializer experimentInitializer;

    @Autowired
    public ExperimentService(ExperimentRepository experimentRepository,
                             CalculationExecutorService executorService,
                             OrikaBeanMapper mapper,
                             DataService dataService,
                             ExperimentConfig experimentConfig, ExperimentInitializer experimentInitializer) {
        this.experimentRepository = experimentRepository;
        this.executorService = executorService;
        this.mapper = mapper;
        this.dataService = dataService;
        this.experimentConfig = experimentConfig;
        this.experimentInitializer = experimentInitializer;
    }

    public ExperimentRequestResult createExperiment(ExperimentRequest experimentRequest) {
        ExperimentRequestResult experimentRequestResult = new ExperimentRequestResult();
        try {
            Experiment experiment = mapper.map(experimentRequest, Experiment.class);
            experiment.setEvaluationStatus(EvaluationStatus.NEW);
            experiment.setCreationDate(LocalDateTime.now());
            experiment.setRetriesToSent(0);
            File dataFile = new File(experimentConfig.getDataStoragePath(),
                    String.format(experimentConfig.getDataFileFormat(), System.currentTimeMillis()));
            dataService.save(dataFile, experimentRequest.getData());
            experiment.setTrainingDataAbsolutePath(dataFile.getAbsolutePath());
            experimentRepository.save(experiment);
            experimentRequestResult.setSuccess(true);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            experimentRequestResult.setErrorMessage(ex.getMessage());
        }
        return experimentRequestResult;
    }

    public void processExperiment(Experiment experiment) {
        experiment.setStartDate(LocalDateTime.now());
        experimentRepository.save(experiment);
        try {
            Instances data = dataService.load(new File(experiment.getTrainingDataAbsolutePath()));
            AbstractExperiment abstractExperiment = experiment.getExperimentType().handle(experimentInitializer, data);

            ExperimentHistory experimentHistory =
                    executorService.execute(new ExperimentProcessor(experiment, abstractExperiment),
                            experimentConfig.getTimeout(), TimeUnit.HOURS);
            File experimentFile = new File(experimentConfig.getStoragePath(),
                    String.format(experimentConfig.getFileFormat(), System.currentTimeMillis()));
            ModelConverter.saveModel(experimentFile, experimentHistory);
            experiment.setExperimentAbsolutePath(experimentFile.getAbsolutePath());
        } catch (TimeoutException ex) {
            log.warn("There was a timeout.");
            experiment.setEvaluationStatus(EvaluationStatus.TIMEOUT);
        } catch (Throwable ex) {
            log.error("There was an error occurred in evaluation : {}", ex);
            experiment.setEvaluationStatus(EvaluationStatus.ERROR);
            experiment.setErrorMessage(ex.getMessage());
        } finally {
            experiment.setEndDate(LocalDateTime.now());
            experimentRepository.save(experiment);
        }
    }

    /**
     *
     */
    private static class ExperimentProcessor implements Callable<ExperimentHistory> {

        Experiment experiment;
        AbstractExperiment abstractExperiment;

        ExperimentProcessor(Experiment experiment, AbstractExperiment abstractExperiment) {
            this.experiment = experiment;
            this.abstractExperiment = abstractExperiment;
        }

        @Override
        public ExperimentHistory call() {
            IterativeExperiment iterativeExperiment = abstractExperiment.getIterativeExperiment();
            while (iterativeExperiment.hasNext()) {
                try {
                    iterativeExperiment.next();
                } catch (Exception ex) {
                    log.warn("Warning for experiment {}: {}", experiment.getId(), ex.getMessage());
                }
            }
            return new ExperimentHistory(abstractExperiment.getHistory(), abstractExperiment.getData());
        }
    }
}
