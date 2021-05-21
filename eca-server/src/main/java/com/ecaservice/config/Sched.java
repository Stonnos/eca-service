package com.ecaservice.config;

import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.web.dto.model.InputOptionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Sched {

    private final SimpMessagingTemplate messagingTemplate;
    private final ExperimentRepository experimentRepository;
    private final ExperimentMapper experimentMapper;

    @Scheduled(fixedDelay = 15000)
    public void s() {
        log.info("Starting to send message");
        Experiment experiment = experimentRepository.findAll().stream().findFirst().orElse(null);
        messagingTemplate.convertAndSend("/queue/experiment", experimentMapper.map(experiment));
        log.info("Message has been sent");
    }
}
