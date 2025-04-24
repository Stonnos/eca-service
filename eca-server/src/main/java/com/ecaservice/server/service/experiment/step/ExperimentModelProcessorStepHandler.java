package com.ecaservice.server.service.experiment.step;

import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.exception.EvaluationCanceledException;
import com.ecaservice.server.exception.EvaluationTimeoutException;
import com.ecaservice.server.exception.experiment.ExperimentException;
import com.ecaservice.server.model.EvaluationStatus;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStep;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.model.experiment.ExperimentContext;
import com.ecaservice.server.model.experiment.ExperimentProcessResult;
import com.ecaservice.server.model.experiment.InitializationParams;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.TaskWorker;
import com.ecaservice.server.service.data.InstancesLoaderService;
import com.ecaservice.server.service.experiment.ExperimentModelLocalStorage;
import com.ecaservice.server.service.experiment.ExperimentProcessorService;
import com.ecaservice.server.service.experiment.ExperimentProgressService;
import com.ecaservice.server.service.experiment.ExperimentStepService;
import eca.dataminer.AbstractExperiment;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import weka.core.Attribute;
import weka.core.Instances;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
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
    private final ExecutorService executorService;
    private final ExperimentStepService experimentStepService;
    private final ExperimentModelLocalStorage experimentModelLocalStorage;
    private final ExperimentProgressService experimentProgressService;
    private final ExperimentRepository experimentRepository;

    /**
     * Constructor with parameters.
     *
     * @param experimentConfig            - experiment config
     * @param instancesLoaderService      - instances loader service
     * @param experimentProcessorService  - experiment processor service
     * @param executorService             - executor service
     * @param experimentStepService       - experiment step service
     * @param experimentModelLocalStorage - experiment model local storage
     * @param experimentProgressService   - experiment progress service
     * @param experimentRepository        - experiment repository
     */
    public ExperimentModelProcessorStepHandler(ExperimentConfig experimentConfig,
                                               InstancesLoaderService instancesLoaderService,
                                               ExperimentProcessorService experimentProcessorService,
                                               ExecutorService executorService,
                                               ExperimentStepService experimentStepService,
                                               ExperimentModelLocalStorage experimentModelLocalStorage,
                                               ExperimentProgressService experimentProgressService,
                                               ExperimentRepository experimentRepository) {
        super(ExperimentStep.EXPERIMENT_PROCESSING);
        this.experimentConfig = experimentConfig;
        this.instancesLoaderService = instancesLoaderService;
        this.experimentProcessorService = experimentProcessorService;
        this.executorService = executorService;
        this.experimentStepService = experimentStepService;
        this.experimentModelLocalStorage = experimentModelLocalStorage;
        this.experimentProgressService = experimentProgressService;
        this.experimentRepository = experimentRepository;
    }

    @NewSpan("processExperimentModelStep")
    @Override
    public void handle(ExperimentContext experimentContext,
                       ExperimentStepEntity experimentStepEntity) {
        try {
            experimentStepService.start(experimentStepEntity);
            Instances data = getInstances(experimentContext);
            processExperiment(data, experimentContext);
            saveModelToLocalStorage(experimentStepEntity, experimentContext.getExperimentHistory());
            saveMaxPctCorrectValue(experimentContext);
            Experiment experiment = experimentContext.getExperiment();
            experiment.setEvaluationTimeMillis(experimentContext.getStopWatch().lastTaskInfo().getTimeMillis());
            experimentRepository.save(experiment);
            experimentProgressService.finish(experimentStepEntity.getExperiment());
            experimentStepService.complete(experimentStepEntity);
        } catch (ObjectStorageException ex) {
            log.error("Object storage error while process experiment [{}]: {}",
                    experimentContext.getExperiment().getRequestId(), ex.getMessage());
            experimentStepService.failed(experimentStepEntity, ex.getMessage());
        } catch (EvaluationTimeoutException ex) {
            log.error("Timeout error while process experiment [{}] model: {}",
                    experimentContext.getExperiment().getRequestId(), ex.getMessage());
            experimentStepService.timeout(experimentStepEntity);
        } catch (EvaluationCanceledException ex) {
            log.warn("Experiment [{}] request processing has been canceled: {}",
                    experimentContext.getExperiment().getRequestId(), ex.getMessage());
            //TODO may be db save
            experimentStepEntity.setStatus(ExperimentStepStatus.CANCELED);
        } catch (Exception ex) {
            log.error("Error while process experiment [{}] model: {}",
                    experimentContext.getExperiment().getRequestId(), ex.getMessage());
            experimentStepService.completeWithError(experimentStepEntity, ex.getMessage());
        }
    }

    private Instances getInstances(ExperimentContext experimentContext) {
        StopWatch stopWatch = experimentContext.getStopWatch();
        Experiment experiment = experimentContext.getExperiment();
        stopWatch.start(String.format("Loading data for experiment [%s]", experiment.getRequestId()));
        Instances data = instancesLoaderService.loadInstances(experiment.getTrainingDataUuid());
        Attribute classAttribute = data.attribute(experiment.getInstancesInfo().getClassName());
        data.setClass(classAttribute);
        stopWatch.stop();
        return data;
    }

    private void processExperiment(Instances data, ExperimentContext experimentContext) throws Exception {
        StopWatch stopWatch = experimentContext.getStopWatch();
        Experiment experiment = experimentContext.getExperiment();
        final InitializationParams initializationParams =
                new InitializationParams(data, experiment.getEvaluationMethod());
        stopWatch.start(String.format("Experiment [%s] processing", experiment.getRequestId()));
        TaskWorker<ExperimentProcessResult> taskWorker = new TaskWorker<>(executorService);
        try {
            Callable<ExperimentProcessResult> callable = () ->
                    experimentProcessorService.processExperimentHistory(experiment, taskWorker, initializationParams);
            var experimentProcessResult =
                    taskWorker.performTask(callable, experimentConfig.getEvaluationTimeoutMinutes(), TimeUnit.MINUTES);
            if (EvaluationStatus.CANCELED.equals(experimentProcessResult.getEvaluationStatus())) {
                throw new EvaluationCanceledException("Experiment request has been canceled");
            }
            if (CollectionUtils.isEmpty(experimentProcessResult.getExperimentHistory().getHistory())) {
                throw new ExperimentException("No models has been built!");
            }
            experimentContext.setEvaluationStatus(experimentProcessResult.getEvaluationStatus());
            experimentContext.setExperimentHistory(experimentProcessResult.getExperimentHistory());
        } catch (TimeoutException ex) {
            taskWorker.cancel();
            log.warn("Experiment evaluation [{}] has been cancelled by timeout",
                    experimentContext.getExperiment().getRequestId());
            throw new EvaluationTimeoutException(ex.getMessage());
        } finally {
            stopWatch.stop();
        }
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
    }

    private void saveModelToLocalStorage(ExperimentStepEntity experimentStepEntity,
                                         AbstractExperiment<?> experimentHistory) throws IOException {
        experimentModelLocalStorage.saveModelAsZip(experimentStepEntity.getExperiment().getRequestId(),
                experimentHistory);
    }
}
