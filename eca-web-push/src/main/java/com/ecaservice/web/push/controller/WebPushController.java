package com.ecaservice.web.push.controller;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.web.dto.model.push.PushRequestDto;
import com.ecaservice.web.push.config.ws.QueueConfig;
import com.ecaservice.web.push.exception.InvalidMessageTypeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
     * Send web push.
     *
     * @param pushRequestDto - push request dto
     */
    @Operation(
            description = "Send web push",
            summary = "Send web push",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "PushRequest",
                                    ref = "#/components/examples/PushRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "InvalidMessageTypeResponse",
                                                    ref = "#/components/examples/InvalidMessageTypeResponse"
                                            )
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/send")
    public void sentPush(@Valid @RequestBody PushRequestDto pushRequestDto) {
        log.info("Received push request [{}], message type [{}], additional properties {}",
                pushRequestDto.getRequestId(), pushRequestDto.getMessageType(),
                pushRequestDto.getAdditionalProperties());
        String queue = queueConfig.getBindings().get(pushRequestDto.getMessageType());
        if (StringUtils.isEmpty(queue)) {
            throw new InvalidMessageTypeException(
                    String.format("Invalid message type [%s]", pushRequestDto.getMessageType()));
        } else {
            log.info("Starting to sent push request [{}, [{}]] to queue [{}]", pushRequestDto.getRequestId(),
                    pushRequestDto.getMessageType(), queue);
            messagingTemplate.convertAndSend(queue, pushRequestDto);
            log.info("Push request [{}, [{}]] has been send to queue [{}]", pushRequestDto.getRequestId(),
                    pushRequestDto.getMessageType(), queue);
        }
    }
}
