package com.ecaservice.web.push.controller.web;

import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.UserNotificationDto;
import com.ecaservice.web.push.service.UserNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;

/**
 * Implements REST API for user notifications.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "User notifications API")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class UserNotificationController {

    private final UserNotificationService userNotificationService;

    /**
     * Finds current user notifications with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return users page
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Finds current user notifications with specified options such as filter, sorting and paging",
            summary = "Finds current user notifications with specified options such as filter, sorting and paging",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB)
    )
    @PostMapping(value = "/list")
    public PageDto<UserNotificationDto> getNotifications(@Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received user notifications page request: {}", pageRequestDto);
        return userNotificationService.getNextPage(pageRequestDto);
    }
}
