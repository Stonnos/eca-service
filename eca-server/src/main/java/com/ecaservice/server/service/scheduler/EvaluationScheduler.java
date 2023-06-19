package com.ecaservice.server.service.scheduler;

import com.ecaservice.server.service.evaluation.ClassifiersDataCleaner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Evaluation scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationScheduler {

   private final ClassifiersDataCleaner classifiersDataCleaner;

    /**
     * Removes evaluation data files from S3. Schedules by cron.
     */
    @Scheduled(cron = "${app.removeModelCron}")
    public void processRequestsToRemove() {
        log.info("Starting job to removes evaluation data files from disk");
        classifiersDataCleaner.removeModels();
        log.info("Removing evaluation data files job has been finished");
    }

}
