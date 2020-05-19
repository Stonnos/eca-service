package com.ecaservice.oauth.controller;

import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.mapping.UserMapper;
import com.ecaservice.user.model.UserDetailsImpl;
import com.ecaservice.web.dto.model.UserDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Implements users REST API.
 *
 * @author Roman Batygin
 */
@Slf4j
@Api(tags = "Users API for web application")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;

    /**
     * Gets current authenticated user info.
     *
     * @return users list
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Gets current authenticated user info",
            notes = "Gets current authenticated user info"
    )
    @GetMapping(value = "/user-info")
    public UserDto getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userMapper.map(userDetails);
    }

    /**
     * Creates new user.
     *
     * @param createUserDto - create user dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Creates new user",
            notes = "Creates new user"
    )
    @PostMapping(value = "/create")
    public void save(@Valid @RequestBody CreateUserDto createUserDto) {
        log.info("User {}", createUserDto);
    }
}
