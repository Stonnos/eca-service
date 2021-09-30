package com.ecaservice.server.service.push;

import com.ecaservice.server.mapping.ExperimentMapper;
import com.ecaservice.server.model.entity.Experiment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for sending web pushes to client.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebPushService {

    private final ExperimentMapper experimentMapper;
    private final WebPushClient webPushClient;

    /**
     * Send web push with experiment changes.
     *
     * @param experiment - experiment entity
     */
    public void sendWebPush(Experiment experiment) {
        log.info("Starting to sent web push for experiment [{}], request status [{}]", experiment.getRequestId(),
                experiment.getRequestStatus());
        try {
            var experimentDto = experimentMapper.map(experiment);
            webPushClient.push(experimentDto);
            log.info("Web push has been sent for experiment [{}], request status [{}]", experiment.getRequestId(),
                    experiment.getRequestStatus());
        } catch (Exception ex) {
            log.error("There was an error while sending web push for experiment [{}], status [{}]: {}",
                    experiment.getRequestId(), experiment.getRequestStatus(), ex.getMessage());
        }
    }
}
