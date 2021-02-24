package com.ecaservice.oauth.controller;

import com.ecaservice.oauth.dto.ChangePasswordRequest;
import com.ecaservice.oauth.entity.ChangePasswordRequestEntity;
import com.ecaservice.oauth.service.ChangePasswordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Implements change password REST API.
 *
 * @author Roman Batygin
 */
@Slf4j
@Api(tags = "Change password API")
@RestController
@RequestMapping("/password")
@RequiredArgsConstructor
public class ChangePasswordController {

    private final ChangePasswordService changePasswordService;

    /**
     * Creates change password request.
     *
     * @param changePasswordRequest - change password request
     */
    @ApiOperation(
            value = "Creates change password request",
            notes = "Creates change password request"
    )
    @PostMapping(value = "/change-request")
    public void createChangePasswordRequest(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        log.info("Received change password request for user [{}]", changePasswordRequest.getUserId());
        ChangePasswordRequestEntity changePasswordRequestEntity =
                changePasswordService.getOrSaveChangePasswordRequest(changePasswordRequest);
        log.info("Change password request [{}] has been created for user [{}]",
                changePasswordRequestEntity.getId(), changePasswordRequest.getUserId());
    }
}
