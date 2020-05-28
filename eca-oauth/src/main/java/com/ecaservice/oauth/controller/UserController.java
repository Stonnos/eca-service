package com.ecaservice.oauth.controller;

import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.event.model.UserCreatedEvent;
import com.ecaservice.oauth.mapping.UserMapper;
import com.ecaservice.oauth.service.PasswordService;
import com.ecaservice.oauth.service.UserService;
import com.ecaservice.user.model.UserDetailsImpl;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.UserDto;
import com.ecaservice.web.dto.model.ValidationErrorDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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

    private final UserService userService;
    private final PasswordService passwordService;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

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
     * Finds all users with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return users page
     */
    @PreAuthorize("#oauth2.hasScope('web') and hasRole('ROLE_SUPER_ADMIN')")
    @ApiOperation(
            value = "Finds users with specified options",
            notes = "Finds users with specified options"
    )
    @GetMapping(value = "/list")
    public PageDto<UserDto> getUsers(@Valid PageRequestDto pageRequestDto) {
        log.info("Received users page request: {}", pageRequestDto);
        Page<UserEntity> usersPage = userService.getNextPage(pageRequestDto);
        List<UserDto> userDtoList = userMapper.map(usersPage.getContent());
        return PageDto.of(userDtoList, pageRequestDto.getPage(), usersPage.getTotalElements());
    }

    /**
     * Creates new user.
     *
     * @param createUserDto - create user dto
     */
    @PreAuthorize("#oauth2.hasScope('web') and hasRole('ROLE_SUPER_ADMIN')")
    @ApiOperation(
            value = "Creates new user",
            notes = "Creates new user"
    )
    @PostMapping(value = "/create")
    public UserDto save(@Valid @RequestBody CreateUserDto createUserDto) {
        log.info("Received request for user creation {}", createUserDto);
        String password = passwordService.generatePassword();
        UserEntity userEntity = userService.createUser(createUserDto, password);
        log.info("User {} has been created", userEntity.getLogin());
        applicationEventPublisher.publishEvent(new UserCreatedEvent(this, userEntity, password));
        return userMapper.map(userEntity);
    }

    /**
     * Handles validation error.
     *
     * @param ex -  method argument not valid exception
     * @return response entity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationErrorDto>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<ValidationErrorDto> errors = ex.getBindingResult().getAllErrors().stream()
                .map(FieldError.class::cast)
                .map(fieldError -> new ValidationErrorDto(fieldError.getField(), fieldError.getCode(),
                        fieldError.getDefaultMessage())).collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }
}
