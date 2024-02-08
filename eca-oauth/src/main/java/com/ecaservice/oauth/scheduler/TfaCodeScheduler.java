package com.ecaservice.oauth.scheduler;

import com.ecaservice.oauth.service.tfa.TfaCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Tfa code scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TfaCodeScheduler {

    private final TfaCodeService tfaCodeService;

    /**
     * Deletes expired tfa codes.
     */
    @Scheduled(fixedDelayString = "${tfa.delaySeconds}000")
    public void deleteExpiredCodes() {
        log.debug("Starting job to delete expired tfa codes");
        tfaCodeService.deleteExpiredCodes();
        log.debug("Expired tfa codes job has been finished");
    }
}
