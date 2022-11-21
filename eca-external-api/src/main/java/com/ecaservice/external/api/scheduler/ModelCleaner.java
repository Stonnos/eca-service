package com.ecaservice.external.api.scheduler;

import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.external.api.repository.InstancesRepository;
import com.ecaservice.external.api.service.EcaRequestService;
import com.ecaservice.external.api.service.InstancesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.common.web.util.PageHelper.processWithPagination;

/**
 * Models cleaner.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ModelCleaner {

    private final ExternalApiConfig externalApiConfig;
    private final EcaRequestService ecaRequestService;
    private final InstancesService instancesService;
    private final EvaluationRequestRepository evaluationRequestRepository;
    private final InstancesRepository instancesRepository;

    /**
     * Removes classifiers data files from S3 storage. Schedules by cron.
     */
    @Scheduled(cron = "${external-api.removeClassifiersCron}")
    public void clearClassifiers() {
        log.info("Starting to remove classifiers data.");
        LocalDateTime dateTime = LocalDateTime.now().minusDays(externalApiConfig.getNumberOfDaysForStorage());
        List<Long> ids = evaluationRequestRepository.findNotDeletedModels(dateTime);
        log.info("Obtained {} classifiers files to remove", ids.size());
        processWithPagination(ids, evaluationRequestRepository::findByIdIn,
                pageContent -> pageContent.forEach(ecaRequestService::deleteClassifierModel),
                externalApiConfig.getBatchSize()
        );
        log.info("Classifiers data removing has been finished.");
    }

    /**
     * Removes train data from S3 storage. Schedules by cron.
     */
    @Scheduled(cron = "${external-api.removeDataCron}")
    public void clearData() {
        log.info("Starting to remove train data.");
        LocalDateTime dateTime = LocalDateTime.now().minusDays(externalApiConfig.getNumberOfDaysForStorage());
        List<Long> ids = instancesRepository.findNotDeletedData(dateTime);
        log.info("Obtained {} data files to remove", ids.size());
        processWithPagination(ids, instancesRepository::findByIdIn,
                pageContent -> pageContent.forEach(instancesService::deleteInstances),
                externalApiConfig.getBatchSize()
        );
        log.info("Train data removing has been finished.");
    }
}
