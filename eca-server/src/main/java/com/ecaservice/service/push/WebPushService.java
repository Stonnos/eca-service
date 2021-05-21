package com.ecaservice.service.push;

import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.model.entity.Experiment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebPushService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ExperimentMapper experimentMapper;

    public void sendWebPush(Experiment experiment) {
        try {
            messagingTemplate.convertAndSend("/queue/experiment", experimentMapper.map(experiment));
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }
}
