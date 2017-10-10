package com.ecaservice.service.experiment;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.mapping.OrikaBeanMapper;
import com.ecaservice.model.EvaluationStatus;
import com.ecaservice.model.experiment.ExperimentRequest;
import com.ecaservice.model.experiment.ExperimentRequestResult;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.InitializationParams;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.CalculationExecutorService;
import eca.converters.ModelConverter;
import eca.converters.model.ExperimentHistory;
import eca.core.EvaluationMethod;
import eca.core.evaluation.EvaluationResults;
import eca.dataminer.AbstractExperiment;
import eca.dataminer.ClassifierComparator;
import eca.dataminer.IterativeExperiment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import weka.core.Instances;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Experiment service.
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ExperimentService {

    private static final int PROGRESS_STEP = 10;

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

    /**
     * Creates experiment request.
     * @param experimentRequest {@link ExperimentRequest} object
     * @return {@link ExperimentRequestResult} object
     */
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

    /**
     * Processes experiment.
     * @param experiment {@link Experiment} object
     */
    public void processExperiment(Experiment experiment) {
        log.info("Starting experiment#{}.", experiment.getId());
        experiment.setStartDate(LocalDateTime.now());
        experimentRepository.save(experiment);
        try {
            StopWatch stopWatch = new StopWatch(String.format("Stop watching for experiment#%d", experiment.getId()));

            stopWatch.start(String.format("Loading data for experiment#%d", experiment.getId()));
            Instances data = dataService.load(new File(experiment.getTrainingDataAbsolutePath()));
            stopWatch.stop();
            InitializationParams initializationParams = new InitializationParams(data,
                    mapper.map(experiment.getEvaluationMethod(), EvaluationMethod.class));

            stopWatch.start(String.format("Experiment#%d processing", experiment.getId()));
            ExperimentHistory experimentHistory =
                    executorService.execute(new ExperimentProcessor(experiment, initializationParams),
                            experimentConfig.getTimeout(), TimeUnit.HOURS);
            stopWatch.stop();

            stopWatch.start(String.format("Experiment#%d saving", experiment.getId()));
            File experimentFile = new File(experimentConfig.getStoragePath(),
                    String.format(experimentConfig.getFileFormat(), System.currentTimeMillis()));
            dataService.save(experimentFile, experimentHistory);
            stopWatch.stop();

            experiment.setExperimentAbsolutePath(experimentFile.getAbsolutePath());
            experiment.setUuid(UUID.randomUUID().toString());
            experiment.setEvaluationStatus(EvaluationStatus.FINISHED);

            log.info("Experiment#{} has been successfully finished!", experiment.getId());
            log.info(stopWatch.prettyPrint());
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
     * Finds experiment file by uuid.
     * @param uuid uuid
     * @return experiment file object
     */
    public File findExperimentByUuid(String uuid) {
       Experiment experiment = experimentRepository.findByUuid(uuid);
       if (experiment == null) {
           log.warn("Experiment for uuid = {} not found!", uuid);
           return null;
       }
       if (experiment.getExperimentAbsolutePath() == null) {
           log.warn("Experiment file for uuid = {} not found!", uuid);
           return null;
       } else {
           return new File(experiment.getExperimentAbsolutePath());
       }
    }

    /**
     * Implements experiment processing.
     */
    private class ExperimentProcessor implements Callable<ExperimentHistory> {

        Experiment experiment;
        InitializationParams initializationParams;
        int currentPercent;

        ExperimentProcessor(Experiment experiment, InitializationParams initializationParams) {
            this.experiment = experiment;
            this.initializationParams = initializationParams;
        }

        @Override
        public ExperimentHistory call() {
            AbstractExperiment abstractExperiment = experiment.getExperimentType()
                    .handle(experimentInitializer, initializationParams);
            IterativeExperiment iterativeExperiment = abstractExperiment.getIterativeExperiment();

            while (iterativeExperiment.hasNext()) {
                try {
                    iterativeExperiment.next();
                    int percent = iterativeExperiment.getPercent();
                    if (percent != currentPercent && percent % PROGRESS_STEP == 0) {
                        log.info("Experiment#{} progress: {} %.", experiment.getId(), percent);
                    }
                    currentPercent = percent;
                } catch (Exception ex) {
                    log.warn("Warning for experiment#{}: {}", experiment.getId(), ex.getMessage());
                }
            }

            ArrayList<EvaluationResults> experimentHistory = abstractExperiment.getHistory();
            experimentHistory.sort(new ClassifierComparator());
            return new ExperimentHistory(createResults(experimentHistory), abstractExperiment.getData());
        }

        List<EvaluationResults> createResults(ArrayList<EvaluationResults> experimentHistory) {
            if (CollectionUtils.isEmpty(experimentHistory)) {
                throw new RuntimeException("No model was built!");
            }
            if (experimentHistory.size() < experimentConfig.getResultSize()) {
                return experimentHistory;
            } else {
                List<EvaluationResults> resultsList = new ArrayList<>(experimentConfig.getResultSize());
                for (int i = 0; i < experimentConfig.getResultSize(); i++) {
                    resultsList.add(experimentHistory.get(i));
                }
                return resultsList;
            }
        }
    }
}
