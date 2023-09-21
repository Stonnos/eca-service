package com.ecaservice.data.loader.scheduler;

import com.ecaservice.data.loader.config.AppProperties;
import com.ecaservice.data.loader.repository.InstancesRepository;
import com.ecaservice.data.loader.service.InstancesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.common.web.util.PageHelper.processWithPagination;

/**
 * Instances data cleaner.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesDataCleaner {

    private final AppProperties appProperties;
    private final InstancesService instancesService;
    private final InstancesRepository instancesRepository;

    /**
     * Removes train data from S3 storage. Schedules by cron.
     */
    @Scheduled(cron = "${app.removeDataCron}")
    public void clearInstances() {
        log.info("Starting to remove train data.");
        List<Long> ids = instancesRepository.findNotDeletedData(LocalDateTime.now());
        log.info("Obtained {} data files to remove", ids.size());
        processWithPagination(ids, instancesRepository::findByIdIn,
                pageContent -> pageContent.forEach(instancesService::deleteInstances),
                appProperties.getBatchSize()
        );
        log.info("Train data removing has been finished.");
    }
}
