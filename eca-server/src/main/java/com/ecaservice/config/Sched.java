package com.ecaservice.config;

import com.ecaservice.web.dto.model.InputOptionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Sched {

    private final SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedDelay = 5000)
    public void s() {
        log.info("Starting to send message");
        InputOptionDto inputOptionDto = new InputOptionDto();
        inputOptionDto.setOptionName("fd");
        inputOptionDto.setOptionValue("df");
        messagingTemplate.convertAndSend("/queue/experiment", inputOptionDto);
    }
}
