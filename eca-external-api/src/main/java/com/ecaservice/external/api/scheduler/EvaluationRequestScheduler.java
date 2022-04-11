package com.ecaservice.external.api.scheduler;

import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.InstancesEntity;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.external.api.repository.InstancesRepository;
import com.ecaservice.external.api.service.EcaRequestService;
import com.ecaservice.external.api.service.InstancesService;
import com.ecaservice.external.api.service.RequestStageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.common.web.util.PageHelper.processWithPagination;

/**
 * Evaluation request scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EvaluationRequestScheduler {

    private final ExternalApiConfig externalApiConfig;
    private final EcaRequestService ecaRequestService;
    private final RequestStageHandler requestStageHandler;
    private final InstancesService instancesService;
    private final EvaluationRequestRepository evaluationRequestRepository;
    private final InstancesRepository instancesRepository;

    /**
     * Processes exceeded requests.
     */
    @Scheduled(fixedDelayString = "${external-api.delaySeconds}000")
    public void processExceededRequests() {
        log.trace("Starting to processed exceeded requests");
        LocalDateTime exceededTime =
                LocalDateTime.now().minusMinutes(externalApiConfig.getEvaluationRequestTimeoutMinutes());
        List<Long> exceededIds = evaluationRequestRepository.findExceededRequestIds(exceededTime);
        processWithPagination(exceededIds, evaluationRequestRepository::findByIdIn,
                pageContent -> pageContent.forEach(requestStageHandler::handleExceeded),
                externalApiConfig.getBatchSize()
        );
        log.trace("Exceeded requests has been processed");
    }

    /**
     * Removes classifiers data files from disk. Schedules by cron.
     */
    @Scheduled(cron = "${external-api.removeClassifiersCron}")
    public void clearClassifiers() {
        log.info("Starting to remove classifiers data.");
        LocalDateTime dateTime = LocalDateTime.now().minusDays(externalApiConfig.getNumberOfDaysForStorage());
        List<Long> ids = evaluationRequestRepository.findNotDeletedModels(dateTime);
        log.info("Obtained {} classifiers files to remove", ids.size());
        processWithPagination(ids, evaluationRequestRepository::findByIdIn, this::clearClassifiers,
                externalApiConfig.getBatchSize());
        log.info("Classifiers data removing has been finished.");
    }

    /**
     * Removes train data files from disk. Schedules by cron.
     */
    @Scheduled(cron = "${external-api.removeDataCron}")
    public void clearData() {
        log.info("Starting to remove train data.");
        LocalDateTime dateTime = LocalDateTime.now().minusDays(externalApiConfig.getNumberOfDaysForStorage());
        List<Long> ids = instancesRepository.findNotDeletedData(dateTime);
        log.info("Obtained {} data files to remove", ids.size());
        processWithPagination(ids, instancesRepository::findByIdIn, this::clearData, externalApiConfig.getBatchSize());
        log.info("Train data removing has been finished.");
    }

    private void clearClassifiers(List<EvaluationRequestEntity> evaluationRequestEntities) {
        evaluationRequestEntities.forEach(evaluationRequestEntity -> {
            try {
                ecaRequestService.deleteClassifierModel(evaluationRequestEntity);
            } catch (Exception ex) {
                log.error("There was an error while deleting evaluation request [{}] classifier model: {}",
                        evaluationRequestEntity.getCorrelationId(), ex.getMessage());
            }
        });
    }

    private void clearData(List<InstancesEntity> instancesEntities) {
        instancesEntities.forEach(instancesEntity -> {
            try {
                instancesService.deleteInstances(instancesEntity);
            } catch (Exception ex) {
                log.error("There was an error while deleting instances [{}]: {}", instancesEntity.getId(),
                        ex.getMessage());
            }
        });
    }
}
