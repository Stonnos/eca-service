package com.ecaservice.service.experiment.visitor;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatusVisitor;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.experiment.mail.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * Visitor to sent email notification for experiment status.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentEmailVisitor implements RequestStatusVisitor<Void, Experiment> {

    private final NotificationService notificationService;
    private final ExperimentRepository experimentRepository;

    @Override
    public Void caseNew(Experiment experiment) {
        notifyEmail(experiment);
        return null;
    }

    @Override
    public Void caseFinished(Experiment experiment) {
        notifyFinalEmail(experiment);
        return null;
    }

    @Override
    public Void caseTimeout(Experiment experiment) {
        return caseFinished(experiment);
    }

    @Override
    public Void caseError(Experiment experiment) {
        return caseFinished(experiment);
    }

    @Override
    public Void caseInProgress(Experiment experiment) {
        notifyEmail(experiment);
        return null;
    }

    private void notifyEmail(Experiment experiment) {
        notify(experiment, notificationService::notifyByEmail);
    }

    private void notifyFinalEmail(Experiment experiment) {
        notify(experiment, exp -> {
            notificationService.notifyByEmail(exp);
            exp.setSentDate(LocalDateTime.now());
            experimentRepository.save(exp);
        });
    }

    private void notify(Experiment experiment, Consumer<Experiment> consumer) {
        try {
           consumer.accept(experiment);
        } catch (Exception ex) {
            log.error("There was an error while sending email request for experiment [{}]: {}",
                    experiment.getRequestId(), ex.getMessage());
        }
    }
}
