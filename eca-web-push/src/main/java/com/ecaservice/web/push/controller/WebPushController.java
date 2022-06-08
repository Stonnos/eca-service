package com.ecaservice.web.push.controller;

import com.ecaservice.web.dto.model.ExperimentDto;
import com.ecaservice.web.push.config.ws.QueueConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implements REST API for sending web pushes.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "API for sending web pushes")
@RestController
@RequestMapping("/push")
@RequiredArgsConstructor
public class WebPushController {

    private final QueueConfig queueConfig;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send web push with experiment changes.
     *
     * @param experimentDto - experiment dto
     */
    @Operation(
            description = "Send web push with experiment changes",
            summary = "Send web push with experiment changes",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "ExperimentRequest",
                                    ref = "#/components/examples/ExperimentRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200")
            }
    )
    @PostMapping(value = "/experiment")
    public void pushExperiment(@RequestBody ExperimentDto experimentDto) {
        log.info("Received web push request for experiment [{}], request status [{}]", experimentDto.getRequestId(),
                experimentDto.getRequestStatus().getValue());
        messagingTemplate.convertAndSend(queueConfig.getExperimentQueue(), experimentDto);
        log.info("Web push request has been send to queue for experiment [{}], request status [{}]",
                experimentDto.getRequestId(), experimentDto.getRequestStatus().getValue());
    }
}
