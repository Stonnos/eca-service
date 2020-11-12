package com.ecaservice.external.api.scheduler;

import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
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

/**
 * Implements task for classifiers models files cleaning.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClassifiersCleanerTask {

    private final ExternalApiConfig externalApiConfig;
    private final FileDataService fileDataService;
    private final EvaluationRequestRepository evaluationRequestRepository;

    /**
     * Removes experiments data files from disk. Schedules by cron.
     */
    @Scheduled(cron = "${external-api.removeClassifiersCron}")
    public void cleanData() {
        log.info("Starting to remove classifiers data.");
        LocalDateTime dateTime = LocalDateTime.now().minusDays(externalApiConfig.getNumberOfDaysForStorage());
        List<Long> ids = evaluationRequestRepository.findNotDeletedModels(dateTime);
        log.info("Obtained {} classifiers files to remove", ids.size());
        processWithPaging(ids);
        log.info("Classifiers data removing has been finished.");
    }

    private void processWithPaging(List<Long> ids) {
        Pageable pageRequest = PageRequest.of(0, externalApiConfig.getBatchSize());
        Page<EvaluationRequestEntity> page;
        do {
            page = evaluationRequestRepository.findByIdIn(ids, pageRequest);
            if (page == null || !page.hasContent()) {
                log.debug("No one requests has been fetched");
                break;
            } else {
                page.forEach(evaluationRequestEntity -> {
                    boolean deleted = fileDataService.delete(evaluationRequestEntity.getClassifierAbsolutePath());
                    if (deleted) {
                        evaluationRequestEntity.setClassifierAbsolutePath(null);
                        evaluationRequestEntity.setDeletedDate(LocalDateTime.now());
                        evaluationRequestRepository.save(evaluationRequestEntity);
                    }
                });
            }
            pageRequest = page.nextPageable();
        } while (page.hasNext());
    }
}
