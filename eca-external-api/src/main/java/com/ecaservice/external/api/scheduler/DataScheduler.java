package com.ecaservice.external.api.scheduler;

import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.external.api.repository.InstancesRepository;
import com.ecaservice.external.api.service.FileDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Data scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataScheduler {

    private final ExternalApiConfig externalApiConfig;
    private final FileDataService fileDataService;
    private final EvaluationRequestRepository evaluationRequestRepository;
    private final InstancesRepository instancesRepository;

    /**
     * Removes classifiers data files from disk. Schedules by cron.
     */
    @Scheduled(cron = "${external-api.removeClassifiersCron}")
    public void clearClassifiers() {
        log.info("Starting to remove classifiers data.");
        LocalDateTime dateTime = LocalDateTime.now().minusDays(externalApiConfig.getNumberOfDaysForStorage());
        List<Long> ids = evaluationRequestRepository.findNotDeletedModels(dateTime);
        log.info("Obtained {} classifiers files to remove", ids.size());
        processPaging(ids, evaluationRequestRepository::findByIdIn,
                pageContent -> pageContent.forEach(evaluationRequestEntity -> {
                    boolean deleted = fileDataService.delete(evaluationRequestEntity.getClassifierAbsolutePath());
                    if (deleted) {
                        evaluationRequestEntity.setClassifierAbsolutePath(null);
                        evaluationRequestEntity.setDeletedDate(LocalDateTime.now());
                        evaluationRequestRepository.save(evaluationRequestEntity);
                    }
                }));
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
        processPaging(ids, instancesRepository::findByIdIn, pageContent -> pageContent.forEach(instancesEntity -> {
            boolean deleted = fileDataService.delete(instancesEntity.getAbsolutePath());
            if (deleted) {
                instancesRepository.delete(instancesEntity);
            }
        }));
        log.info("Train data removing has been finished.");
    }

    private <T> void processPaging(List<Long> ids,
                                   BiFunction<List<Long>, Pageable, Page<T>> nextPageFunction,
                                   Consumer<List<T>> pageContentAction) {
        Pageable pageRequest = PageRequest.of(0, externalApiConfig.getBatchSize());
        Page<T> page;
        do {
            page = nextPageFunction.apply(ids, pageRequest);
            if (page == null || !page.hasContent()) {
                log.trace("No one record has been fetched");
                break;
            } else {
                pageContentAction.accept(page.getContent());
            }
            pageRequest = page.nextPageable();
        } while (page.hasNext());
    }
}
