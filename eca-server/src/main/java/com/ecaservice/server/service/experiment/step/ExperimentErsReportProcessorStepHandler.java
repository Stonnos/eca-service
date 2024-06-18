package com.ecaservice.server.service.experiment.step;

import com.ecaservice.server.event.model.ExperimentErsReportEvent;
import com.ecaservice.server.model.entity.ExperimentStep;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.experiment.ExperimentContext;
import com.ecaservice.server.service.experiment.ExperimentStepService;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.tracing.annotation.NewSpan;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Step handler for sent experiment results to ERS.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ExperimentErsReportProcessorStepHandler extends AbstractExperimentStepHandler {

    private final ExperimentStepService experimentStepService;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Constructor with parameters.
     *
     * @param experimentStepService     - experiment step service
     * @param applicationEventPublisher - application event publisher
     */
    public ExperimentErsReportProcessorStepHandler(ExperimentStepService experimentStepService,
                                                   ApplicationEventPublisher applicationEventPublisher) {
        super(ExperimentStep.CREATE_ERS_REPORT);
        this.experimentStepService = experimentStepService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @NewSpan("saveExperimentErsReportStep")
    @Override
    public void handle(ExperimentContext experimentContext,
                       ExperimentStepEntity experimentStepEntity) {
        try {
            experimentStepService.start(experimentStepEntity);
            var stopWatch = experimentContext.getStopWatch();
            stopWatch.start(
                    String.format("Sent experiment [%s] ERS report", experimentContext.getExperiment().getRequestId()));
            applicationEventPublisher.publishEvent(new ExperimentErsReportEvent(this,
                    experimentContext.getExperiment(), experimentContext.getExperimentHistory()));
            stopWatch.stop();
            experimentStepService.complete(experimentStepEntity);
        } catch (Exception ex) {
            log.error("Error while sent ers report for experiment [{}]: {}",
                    experimentContext.getExperiment().getRequestId(), ex.getMessage());
            experimentStepService.completeWithError(experimentStepEntity, ex.getMessage());
        }
    }
}
