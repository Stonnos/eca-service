package com.ecaservice.oauth.scheduler;

import com.ecaservice.oauth.config.TfaConfig;
import com.ecaservice.oauth.repository.TfaCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;

import static com.ecaservice.common.web.util.PageHelper.processWithPagination;

/**
 * Tfa code scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TfaCodeScheduler {

    private final TfaConfig tfaConfig;
    private final TfaCodeRepository tfaCodeRepository;

    /**
     * Deletes expired tfa codes.
     */
    @Scheduled(fixedDelayString = "${tfa.delaySeconds}000")
    public void deleteExpiredCodes() {
        log.debug("Starting job to delete expired tfa codes");
        var ids = tfaCodeRepository.getExpiredCodeIds(LocalDateTime.now());
        if (!CollectionUtils.isEmpty(ids)) {
            log.info("Found [{}] expired tfa codes", ids.size());
            processWithPagination(ids, tfaCodeRepository::findByIdIn,
                    tfaCodeRepository::deleteAll, tfaConfig.getPageSize());
            log.info("[{}] expired tfa codes has been removed", ids.size());
        }
        log.debug("Expired tfa codes job has been finished");
    }
}
