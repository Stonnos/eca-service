package com.ecaservice.service.scheduler;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsModel;
import com.ecaservice.model.entity.ExperimentResultsRequest;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.model.experiment.ExperimentResultsRequestStatus;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsRequestRepository;
import com.ecaservice.service.EvaluationResultsService;
import com.ecaservice.service.PageableCallback;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.service.experiment.mail.NotificationService;
import eca.core.evaluation.EvaluationResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Experiment scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ExperimentScheduler {

    private final ExperimentRepository experimentRepository;
    private final ExperimentResultsRequestRepository experimentResultsRequestRepository;
    private final ExperimentService experimentService;
    private final NotificationService notificationService;
    private final EvaluationResultsService evaluationResultsService;
    private final ExperimentConfig experimentConfig;

    /**
     * Constructor with dependency spring injection.
     *
     * @param experimentRepository               - experiment repository bean
     * @param experimentResultsRequestRepository - experiment results request repository bean
     * @param experimentService                  - experiment service bean
     * @param notificationService                - notification service bean
     * @param evaluationResultsService           - evaluation results service bean
     * @param experimentConfig                   - experiment config bean
     */
    @Inject
    public ExperimentScheduler(ExperimentRepository experimentRepository,
                               ExperimentResultsRequestRepository experimentResultsRequestRepository,
                               ExperimentService experimentService,
                               NotificationService notificationService,
                               EvaluationResultsService evaluationResultsService,
                               ExperimentConfig experimentConfig) {
        this.experimentRepository = experimentRepository;
        this.experimentResultsRequestRepository = experimentResultsRequestRepository;
        this.experimentService = experimentService;
        this.notificationService = notificationService;
        this.evaluationResultsService = evaluationResultsService;
        this.experimentConfig = experimentConfig;
    }

    /**
     * Processing new experiment requests.
     */
    @Scheduled(fixedDelayString = "${experiment.delaySeconds}000")
    public void processNewRequests() {
        log.trace("Starting to built experiments.");
        processExperiments(new PageableCallback<Experiment>() {
            @Override
            public void perform(List<Experiment> experiments) {
                experiments.forEach(experiment -> {
                    experimentService.processExperiment(experiment);
                    if (ExperimentStatus.FINISHED.equals(experiment.getExperimentStatus())) {
                        ExperimentResultsRequest resultsRequest = new ExperimentResultsRequest();
                        resultsRequest.setRequestStatus(ExperimentResultsRequestStatus.NEW);
                        resultsRequest.setRequestSource(ExperimentResultsRequestSource.SYSTEM);
                        resultsRequest.setCreationDate(LocalDateTime.now());
                        resultsRequest.setExperiment(experiment);
                        experimentResultsRequestRepository.save(resultsRequest);
                        log.info("Experiment results request has been created for experiment: {}", experiment.getId());
                    }
                });
            }

            @Override
            public Page<Experiment> findNextPage(Pageable pageable) {
                return experimentRepository.findNotSentExperiments(Collections.singletonList(ExperimentStatus.NEW),
                        pageable);
            }
        });
        log.trace("Building experiments has been successfully finished.");
    }

    /**
     * Processing experiment requests for sending.
     */
    @Scheduled(fixedDelayString = "${experiment.delaySeconds}000")
    public void processRequestsToSent() {
        log.trace("Starting to sent experiment results.");
        processExperiments(new PageableCallback<Experiment>() {
            @Override
            public void perform(List<Experiment> experiments) {
                experiments.forEach(notificationService::notifyByEmail);
            }

            @Override
            public Page<Experiment> findNextPage(Pageable pageable) {
                return experimentRepository.findNotSentExperiments(
                        Arrays.asList(ExperimentStatus.FINISHED, ExperimentStatus.ERROR, ExperimentStatus.TIMEOUT),
                        pageable);
            }
        });
        log.trace("Sending experiments has been successfully finished.");
    }

    /**
     * Removes experiments data files from disk. Schedules by cron.
     */
    @Scheduled(cron = "${experiment.removeExperimentCron}")
    public void processRequestsToRemove() {
        log.info("Starting to remove experiments data.");
        final LocalDateTime dateTime = LocalDateTime.now().minusDays(experimentConfig.getNumberOfDaysForStorage());
        processExperiments(new PageableCallback<Experiment>() {
            @Override
            public void perform(List<Experiment> experiments) {
                experiments.forEach(experimentService::removeExperimentData);
            }

            @Override
            public Page<Experiment> findNextPage(Pageable pageable) {
                return experimentRepository.findNotDeletedExperiments(dateTime, pageable);
            }
        });
        log.info("Experiments data removing has been finished.");
    }

    /**
     * Sends experiment results to ERS web - service.
     */
    @Scheduled(cron = "${experiment.sendResultsCron}")
    public void processExperimentResultsRequests() {
        log.info("Starting to send experiment results to evaluation-results-service.");
        processExperiments(new PageableCallback<ExperimentResultsRequest>() {
            @Override
            public void perform(List<ExperimentResultsRequest> resultsRequests) {
                for (ExperimentResultsRequest request : resultsRequests) {
                    try {
                        Collection<EvaluationResults> evaluationResults =
                                experimentService.getEvaluationResults(request.getExperiment());
                        int resultsSize = Integer.min(evaluationResults.size(), experimentConfig.getResultSizeToSend());
                        evaluationResults.stream().limit(resultsSize).forEach(results -> {
                            ExperimentResultsModel resultsModel = new ExperimentResultsModel();
                            resultsModel.setExperimentResultsRequest(request);
                            evaluationResultsService.saveEvaluationResults(results, resultsModel);
                        });
                        request.setRequestStatus(ExperimentResultsRequestStatus.COMPLETE);
                    } catch (Exception ex) {
                        log.error("There was an error: {}", ex.getMessage());
                        request.setRequestStatus(ExperimentResultsRequestStatus.ERROR);
                        request.setDetails(ex.getMessage());
                    } finally {
                        request.setSentDate(LocalDateTime.now());
                        experimentResultsRequestRepository.save(request);
                    }
                }
            }

            @Override
            public Page<ExperimentResultsRequest> findNextPage(Pageable pageable) {
                return experimentResultsRequestRepository.findNotSentExperimentRequests(
                        Collections.singletonList(ExperimentResultsRequestStatus.NEW), pageable);
            }
        });
        log.info("Processing experiment results requests has been finished.");
    }

    /**
     * Processes experiments using pagination. Method <code>findNextPage</code> always takes
     * the initial page request, because experiments fields are updated at each iteration.
     *
     * @param pageableCallback callback {@link PageableCallback}
     */
    private <T> void processExperiments(PageableCallback<T> pageableCallback) {
        Pageable pageable = new PageRequest(0, experimentConfig.getPageSize());
        Page<T> page;
        int totalCount = 0;
        do {
            page = pageableCallback.findNextPage(pageable);
            if (!page.hasContent()) {
                break;
            }
            log.trace("Obtained new experiments page [{}, {}] for processing.", page.getNumber(), page.getSize());
            pageableCallback.perform(page.getContent());
            totalCount += page.getSize();
            log.trace("Experiments page [{}, {}] has been processed.", page.getNumber(), page.getSize());
        } while (page.hasNext());
        log.trace("{} experiments has been processed.", totalCount);
    }

}
