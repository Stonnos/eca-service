package com.ecaservice.external.api.scheduler;

import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.repository.InstancesRepository;
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
    private final InstancesService instancesService;
    private final InstancesRepository instancesRepository;

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
