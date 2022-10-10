package com.ecaservice.web.push.controller.api;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.web.push.dto.AbstractPushRequest;
import com.ecaservice.web.push.service.handler.AbstractPushRequestHandler;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Implements REST API for sending web pushes.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "API for sending web pushes")
@RestController
@RequestMapping("/api/push")
@RequiredArgsConstructor
public class WebPushController {

    private final List<AbstractPushRequestHandler> pushRequestHandlers;

    /**
     * Send push notification.
     *
     * @param pushRequest - user push notification
     */
    @Operation(
            description = "Send user push notification",
            summary = "Send user push notification",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "SystemPushRequest",
                                    ref = "#/components/examples/SystemPushRequest"
                            ),
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
    @PostMapping(value = "/send")
    @SuppressWarnings("unchecked")
    public void sentPushNotification(@Valid @RequestBody AbstractPushRequest pushRequest) {
        log.info("Received push request [{}] with type [{}], message code [{}]", pushRequest.getRequestId(),
                pushRequest.getPushType(), pushRequest.getMessageType());
        var handler = getRequestHandler(pushRequest);
        handler.handle(pushRequest);
        log.info("Push request [{}] with type [{}], message code [{}] has been processed", pushRequest.getRequestId(),
                pushRequest.getPushType(), pushRequest.getMessageType());
    }

    private AbstractPushRequestHandler getRequestHandler(AbstractPushRequest pushRequest) {
        return pushRequestHandlers.stream()
                .filter(handler -> handler.canHandle(pushRequest))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't handle push type [%s]", pushRequest.getPushType())));
    }
}
