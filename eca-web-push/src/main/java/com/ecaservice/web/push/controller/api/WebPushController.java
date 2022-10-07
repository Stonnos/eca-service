package com.ecaservice.web.push.controller.api;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.web.dto.model.push.PushRequestDto;
import com.ecaservice.web.push.config.ws.QueueConfig;
import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import com.ecaservice.web.push.service.UserNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UserNotificationService userNotificationService;

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
                                                    name = "InvalidPushRequestResponse",
                                                    ref = "#/components/examples/InvalidPushRequestResponse"
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
        String queue = queueConfig.getPushQueue();
        log.info("Starting to sent push request [{}, [{}]] to queue [{}]", pushRequestDto.getRequestId(),
                pushRequestDto.getMessageType(), queue);
        messagingTemplate.convertAndSend(queue, pushRequestDto);
        log.info("Push request [{}, [{}]] has been send to queue [{}]", pushRequestDto.getRequestId(),
                pushRequestDto.getMessageType(), queue);
    }

    /**
     * Send user push notification.
     *
     * @param userPushNotificationRequest - user push notification
     */
    @Operation(
            description = "Send user push notification",
            summary = "Send user push notification",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "UserNotificationPushRequest",
                                    ref = "#/components/examples/UserNotificationPushRequest"
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
                                                    name = "InvalidPushRequestResponse",
                                                    ref = "#/components/examples/InvalidPushRequestResponse"
                                            )
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/user-notification")
    public void sentUserPushNotification(@Valid @RequestBody UserPushNotificationRequest userPushNotificationRequest) {
        log.info("Received user notification push request [{}]", userPushNotificationRequest.getRequestId());
        userNotificationService.save(userPushNotificationRequest);
    }
}
