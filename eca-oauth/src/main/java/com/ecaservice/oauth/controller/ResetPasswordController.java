package com.ecaservice.oauth.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implements reset password REST API.
 *
 * @author Roman Batygin
 */
@Slf4j
@Api(tags = "Reset password API")
@RestController
@RequestMapping("/password")
@RequiredArgsConstructor
public class ResetPasswordController {
}
