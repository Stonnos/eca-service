package com.ecaservice.server.service.experiment;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.event.model.ExperimentEmailEvent;
import com.ecaservice.server.event.model.ExperimentResponseEvent;
import com.ecaservice.server.event.model.push.ExperimentSystemPushEvent;
import com.ecaservice.server.event.model.push.ExperimentWebPushEvent;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.ChannelVisitor;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.ExperimentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import static com.ecaservice.common.web.util.LogHelper.EV_REQUEST_ID;
import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.server.config.EcaServiceConfiguration.EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN;

/**
 * Experiment request processor.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentRequestProcessor {

    private final ExperimentService experimentService;
    private final ApplicationEventPublisher eventPublisher;
    private final ExperimentRepository experimentRepository;

    /**
     * Starts experiment.
     *
     * @param id - experiment id
     */
    @Locked(lockName = "experiment", key = "#id", lockRegistry = EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN,
            waitForLock = false)
    public void startExperiment(Long id) {
        var experiment = getById(id);
        putMdc(TX_ID, experiment.getRequestId());
        putMdc(EV_REQUEST_ID, experiment.getRequestId());
        if (!RequestStatus.NEW.equals(experiment.getRequestStatus())) {
            log.warn("Attempt to process new experiment [{}] with status [{}]. Skipped...", experiment.getRequestId(),
                    experiment.getRequestStatus());
            return;
        }
        log.info("Starting to process new experiment [{}]", experiment.getRequestId());
        experimentService.startExperiment(experiment);
        eventPublisher.publishEvent(new ExperimentSystemPushEvent(this, experiment));
        if (Channel.WEB.equals(experiment.getChannel())) {
            eventPublisher.publishEvent(new ExperimentWebPushEvent(this, experiment));
        }
        eventPublisher.publishEvent(new ExperimentEmailEvent(this, experiment));
    }

    /**
     * Processes experiment.
     *
     * @param id - experiment id
     */
    @Locked(lockName = "experiment", key = "#id", lockRegistry = EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN,
            waitForLock = false)
    public void processExperiment(Long id) {
        var experiment = getById(id);
        putMdc(TX_ID, experiment.getRequestId());
        putMdc(EV_REQUEST_ID, experiment.getRequestId());
        if (!RequestStatus.IN_PROGRESS.equals(experiment.getRequestStatus())) {
            log.warn("Attempt to process experiment [{}] with status [{}]. Skipped...", experiment.getRequestId(),
                    experiment.getRequestStatus());
            return;
        }
        log.info("Starting to process experiment [{}] request", experiment.getRequestId());
        experimentService.processExperiment(experiment);
        log.info("Experiment [{}] request has been processed", experiment.getRequestId());
    }

    /**
     * Finishes experiment.
     *
     * @param id - experiment id
     */
    @Locked(lockName = "experiment", key = "#id", lockRegistry = EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN,
            waitForLock = false)
    public void finishExperiment(Long id) {
        var experiment = getById(id);
        putMdc(TX_ID, experiment.getRequestId());
        putMdc(EV_REQUEST_ID, experiment.getRequestId());
        if (!RequestStatus.IN_PROGRESS.equals(experiment.getRequestStatus())) {
            log.warn("Attempt to finish experiment [{}] with status [{}]. Skipped...", experiment.getRequestId(),
                    experiment.getRequestStatus());
            return;
        }
        log.info("Starting to finish experiment [{}]", experiment.getRequestId());
        experimentService.finishExperiment(experiment);
        eventPublisher.publishEvent(new ExperimentSystemPushEvent(this, experiment));
        sendFinalResponse(experiment);
        eventPublisher.publishEvent(new ExperimentEmailEvent(this, experiment));
        log.info("Experiment [{}] has been finished", experiment.getRequestId());
    }

    private void sendFinalResponse(Experiment experiment) {
        experiment.getChannel().visit(new ChannelVisitor() {
            @Override
            public void visitWeb() {
                eventPublisher.publishEvent(new ExperimentWebPushEvent(this, experiment));
            }

            @Override
            public void visitQueue() {
                eventPublisher.publishEvent(new ExperimentResponseEvent(this, experiment));
            }
        });
    }

    private Experiment getById(Long id) {
        return experimentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Experiment.class, id));
    }
}
