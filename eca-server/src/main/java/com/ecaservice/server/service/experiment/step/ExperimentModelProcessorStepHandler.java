package com.ecaservice.server.service.experiment.step;

import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.exception.experiment.ExperimentException;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStep;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.experiment.ExperimentContext;
import com.ecaservice.server.model.experiment.InitializationParams;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.data.InstancesLoaderService;
import com.ecaservice.server.service.evaluation.CalculationExecutorService;
import com.ecaservice.server.service.experiment.ExperimentProcessorService;
import com.ecaservice.server.service.experiment.ExperimentProgressService;
import com.ecaservice.server.service.experiment.ExperimentStepService;
import eca.dataminer.AbstractExperiment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import weka.core.Instances;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Step handler for experiment model processing.
 *
 * @author Roman
 */
@Slf4j
@Component
public class ExperimentModelProcessorStepHandler extends AbstractExperimentStepHandler {

    private final ExperimentConfig experimentConfig;
    private final InstancesLoaderService instancesLoaderService;
    private final ExperimentProcessorService experimentProcessorService;
    private final CalculationExecutorService executorService;
    private final ExperimentStepService experimentStepService;
    private final ExperimentProgressService experimentProgressService;
    private final ExperimentRepository experimentRepository;

    /**
     * Constructor with parameters.
     *
     * @param experimentConfig           - experiment config
     * @param instancesLoaderService     - instances loader service
     * @param experimentProcessorService - experiment processor service
     * @param executorService            - executor service
     * @param experimentStepService      - experiment step service
     * @param experimentProgressService  - experiment progress service
     * @param experimentRepository       - experiment repository
     */
    public ExperimentModelProcessorStepHandler(ExperimentConfig experimentConfig,
                                               InstancesLoaderService instancesLoaderService,
                                               ExperimentProcessorService experimentProcessorService,
                                               @Qualifier("calculationExecutorServiceImpl")
                                               CalculationExecutorService executorService,
                                               ExperimentStepService experimentStepService,
                                               ExperimentProgressService experimentProgressService,
                                               ExperimentRepository experimentRepository) {
        super(ExperimentStep.EXPERIMENT_PROCESSING);
        this.experimentConfig = experimentConfig;
        this.instancesLoaderService = instancesLoaderService;
        this.experimentProcessorService = experimentProcessorService;
        this.executorService = executorService;
        this.experimentStepService = experimentStepService;
        this.experimentProgressService = experimentProgressService;
        this.experimentRepository = experimentRepository;
    }

    @Override
    public void handle(ExperimentContext experimentContext,
                       ExperimentStepEntity experimentStepEntity) {
        try {
            Instances data = getInstances(experimentContext);
            processExperiment(data, experimentContext);
            saveMaxPctCorrectValue(experimentContext);
            experimentProgressService.finish(experimentStepEntity.getExperiment());
            experimentStepService.complete(experimentStepEntity);
        } catch (ObjectStorageException ex) {
            log.error("Object storage error while process experiment [{}]: {}",
                    experimentContext.getExperiment().getRequestId(), ex.getMessage());
            experimentStepService.failed(experimentStepEntity, ex.getMessage());
        } catch (TimeoutException ex) {
            log.error("Timeout error while process experiment [{}] model: {}",
                    experimentContext.getExperiment().getRequestId(), ex.getMessage());
            experimentStepService.timeout(experimentStepEntity);
        } catch (Exception ex) {
            log.error("Error while process experiment [{}] model: {}",
                    experimentContext.getExperiment().getRequestId(), ex.getMessage());
            experimentStepService.completeWithError(experimentStepEntity, ex.getMessage());
        }
    }

    private Instances getInstances(ExperimentContext experimentContext) {
        StopWatch stopWatch = experimentContext.getStopWatch();
        Experiment experiment = experimentContext.getExperiment();
        if (StringUtils.isEmpty(experiment.getTrainingDataPath())) {
            throw new ExperimentException(String.format("Training data path is not specified for experiment [%s]!",
                    experiment.getRequestId()));
        }
        stopWatch.start(String.format("Loading data for experiment [%s]", experiment.getRequestId()));
        Instances data = instancesLoaderService.loadInstances(experiment.getTrainingDataPath());
        //TODO set class name from experiment.instances_info.class_name
        data.setClassIndex(experiment.getClassIndex());
        stopWatch.stop();
        return data;
    }

    private void processExperiment(Instances data, ExperimentContext experimentContext) throws Exception {
        StopWatch stopWatch = experimentContext.getStopWatch();
        Experiment experiment = experimentContext.getExperiment();
        final InitializationParams initializationParams =
                new InitializationParams(data, experiment.getEvaluationMethod());
        stopWatch.start(String.format("Experiment [%s] processing", experiment.getRequestId()));
        Callable<AbstractExperiment<?>> callable = () ->
                experimentProcessorService.processExperimentHistory(experiment, initializationParams);
        AbstractExperiment<?> abstractExperiment =
                executorService.execute(callable, experimentConfig.getTimeout(), TimeUnit.HOURS);
        stopWatch.stop();
        experimentContext.setExperimentHistory(abstractExperiment);
    }

    private void saveMaxPctCorrectValue(ExperimentContext experimentContext) {
        Experiment experiment = experimentContext.getExperiment();
        var bestEvaluationResults = experimentContext.getExperimentHistory()
                .getHistory()
                .stream()
                .max(Comparator.comparing(evaluationResults -> evaluationResults.getEvaluation().pctCorrect()))
                .orElseThrow(() -> new ExperimentException(
                        String.format("Can't find best evaluation results for experiment [%s]",
                                experiment.getRequestId())));
        experiment.setMaxPctCorrect(BigDecimal.valueOf(bestEvaluationResults.getEvaluation().pctCorrect()));
        experimentRepository.save(experiment);
    }
}
