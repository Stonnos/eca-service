package com.ecaservice.oauth.controller.web;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.dto.UpdateUserInfoDto;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.entity.UserPhoto;
import com.ecaservice.oauth.event.model.UserCreatedEvent;
import com.ecaservice.oauth.exception.UserLockNotAllowedException;
import com.ecaservice.oauth.mapping.UserMapper;
import com.ecaservice.oauth.repository.UserPhotoRepository;
import com.ecaservice.oauth.service.PasswordService;
import com.ecaservice.oauth.service.UserService;
import com.ecaservice.user.model.UserDetailsImpl;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.UserDto;
import com.ecaservice.web.dto.model.UsersPageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.security.Principal;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;
import static com.ecaservice.oauth.controller.doc.ApiExamples.ACCESS_DENIED_RESPONSE_JSON;
import static com.ecaservice.oauth.controller.doc.ApiExamples.CREATE_USER_REQUEST_JSON;
import static com.ecaservice.oauth.controller.doc.ApiExamples.DATA_NOT_FOUND_RESPONSE_JSON;
import static com.ecaservice.oauth.controller.doc.ApiExamples.INVALID_PAGE_REQUEST_RESPONSE_JSON;
import static com.ecaservice.oauth.controller.doc.ApiExamples.INVALID_TFA_REQUEST_RESPONSE_JSON;
import static com.ecaservice.oauth.controller.doc.ApiExamples.INVALID_UPDATE_USER_INFO_REQUEST_RESPONSE_JSON;
import static com.ecaservice.oauth.controller.doc.ApiExamples.SIMPLE_PAGE_REQUEST_JSON;
import static com.ecaservice.oauth.controller.doc.ApiExamples.UNAUTHORIZED_RESPONSE_JSON;
import static com.ecaservice.oauth.controller.doc.ApiExamples.UNIQUE_LOGIN_RESPONSE_JSON;
import static com.ecaservice.oauth.controller.doc.ApiExamples.UPDATE_USER_INFO_REQUEST_JSON;
import static com.ecaservice.oauth.controller.doc.ApiExamples.USERS_PAGE_RESPONSE_JSON;
import static com.ecaservice.oauth.controller.doc.ApiExamples.USER_DTO_RESPONSE_JSON;
import static com.ecaservice.oauth.util.Utils.buildAttachmentResponse;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Implements users REST API.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Tag(name = "Users API for web application")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordService passwordService;
    private final UserMapper userMapper;
    private final DefaultTokenServices tokenServices;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserPhotoRepository userPhotoRepository;

    /**
     * Gets current authenticated user info.
     *
     * @return users list
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets current authenticated user info",
            summary = "Gets current authenticated user info",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = USER_DTO_RESPONSE_JSON),
                                    },
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    )
            }
    )
    @GetMapping(value = "/user-info")
    public UserDto getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.debug("Request get current user [{}]", userDetails.getId());
        return userService.getUserInfo(userDetails.getId());
    }

    /**
     * Logout current user and revokes access/refresh token pair.
     *
     * @param authentication - oauth2 authentication
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Logout current user and revokes access/refresh token pair",
            summary = "Logout current user and revokes access/refresh token pair",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    )
            }
    )
    @PostMapping(value = "/logout")
    public void logout(Principal authentication) {
        log.info("Request to logout user: [{}]", authentication.getName());
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
        OAuth2AccessToken auth2AccessToken = tokenServices.getAccessToken(oAuth2Authentication);
        tokenServices.revokeToken(auth2AccessToken.getValue());
        log.info("User [{}] has been logout", authentication.getName());
    }

    /**
     * Enable/disable tfa for current authenticated user.
     *
     * @param enabled - tfa enabled?
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Enable/disable tfa for current authenticated user",
            summary = "Enable/disable tfa for current authenticated user",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = INVALID_TFA_REQUEST_RESPONSE_JSON),
                                    }
                            ))
            }
    )
    @PostMapping(value = "/tfa")
    public void tfa(@AuthenticationPrincipal UserDetailsImpl userDetails,
                    @Parameter(description = "Tfa enabled flag", required = true) @RequestParam boolean enabled) {
        if (enabled) {
            userService.enableTfa(userDetails.getId());
        } else {
            userService.disableTfa(userDetails.getId());
        }
    }

    /**
     * Finds all users with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return users page
     */
    @PreAuthorize("#oauth2.hasScope('web') and hasRole('ROLE_SUPER_ADMIN')")
    @Operation(
            description = "Finds users with specified options",
            summary = "Finds users with specified options",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = SIMPLE_PAGE_REQUEST_JSON)
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = USERS_PAGE_RESPONSE_JSON),
                                    },
                                    schema = @Schema(implementation = UsersPageDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Permission denied", responseCode = "403",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = ACCESS_DENIED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = INVALID_PAGE_REQUEST_RESPONSE_JSON),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/list")
    public PageDto<UserDto> getUsers(@Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received users page request: {}", pageRequestDto);
        return userService.getUsersPage(pageRequestDto);
    }

    /**
     * Creates new user.
     *
     * @param createUserDto - create user dto
     */
    @PreAuthorize("#oauth2.hasScope('web') and hasRole('ROLE_SUPER_ADMIN')")
    @Operation(
            description = "Creates new user",
            summary = "Creates new user",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = CREATE_USER_REQUEST_JSON)
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = USER_DTO_RESPONSE_JSON),
                                    },
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Permission denied", responseCode = "403",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = ACCESS_DENIED_RESPONSE_JSON),
                                    }
                            )),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNIQUE_LOGIN_RESPONSE_JSON),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/create")
    public UserDto save(@Valid @RequestBody CreateUserDto createUserDto) {
        log.info("Received request for user creation [{}]", createUserDto.getLogin());
        String password = passwordService.generatePassword();
        UserEntity userEntity = userService.createUser(createUserDto, password);
        log.info("User {} has been created", userEntity.getId());
        applicationEventPublisher.publishEvent(new UserCreatedEvent(this, userEntity, password));
        return userMapper.map(userEntity);
    }

    /**
     * Updates info for current authenticated user.
     *
     * @param userDetails       - user details
     * @param updateUserInfoDto - user info dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Updates info for current authenticated user",
            summary = "Updates info for current authenticated user",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = UPDATE_USER_INFO_REQUEST_JSON)
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = INVALID_UPDATE_USER_INFO_REQUEST_RESPONSE_JSON),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PutMapping(value = "/update-info")
    public void updateUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
                               @Valid @RequestBody UpdateUserInfoDto updateUserInfoDto) {
        log.info("Received request to update user [{}] info", userDetails.getId());
        userService.updateUserInfo(userDetails.getId(), updateUserInfoDto);
    }

    /**
     * Uploads photo for current authenticated user.
     *
     * @param userDetails - user details
     * @param file        - user photo file
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Uploads photo for current authenticated user",
            summary = "Uploads photo for current authenticated user",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400")
            }
    )
    @PostMapping(value = "/upload-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadPhoto(@AuthenticationPrincipal UserDetailsImpl userDetails,
                            @Parameter(description = "Photo file", required = true) @RequestParam MultipartFile file) {
        log.info("Uploads photo [{}] for user [{}]", file.getOriginalFilename(), userDetails.getId());
        userService.updatePhoto(userDetails.getId(), file);
    }

    /**
     * Downloads user photo.
     *
     * @param id - photo id
     * @return user photo as byte array
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Downloads user photo",
            summary = "Downloads user photo",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = DATA_NOT_FOUND_RESPONSE_JSON),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @GetMapping(value = "/photo/{id}")
    public ResponseEntity<ByteArrayResource> downloadPhoto(
            @Parameter(description = "Photo id", required = true, example = "1")
            @Min(VALUE_1) @Max(Long.MAX_VALUE) @PathVariable Long id) {
        UserPhoto userPhoto = userPhotoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UserPhoto.class, id));
        return buildAttachmentResponse(userPhoto.getPhoto(), userPhoto.getFileName());
    }

    /**
     * Deletes photo for current authenticated user
     *
     * @param userDetails - user details
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Deletes photo for current authenticated user",
            summary = "Deletes photo for current authenticated user",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = DATA_NOT_FOUND_RESPONSE_JSON),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @DeleteMapping(value = "/delete-photo")
    public void deletePhoto(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("Deletes photo for user [{}]", userDetails.getId());
        userService.deletePhoto(userDetails.getId());
    }

    /**
     * Locks user.
     *
     * @param userDetails - user details
     * @param userId      - user id
     */
    @PreAuthorize("#oauth2.hasScope('web') and hasRole('ROLE_SUPER_ADMIN')")
    @Operation(
            description = "Locks user",
            summary = "Locks user",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Permission denied", responseCode = "403",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = ACCESS_DENIED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400")
            }
    )
    @PostMapping(value = "/lock")
    public void lock(@AuthenticationPrincipal UserDetailsImpl userDetails,
                     @Parameter(description = "User id", example = "1", required = true)
                     @Min(VALUE_1) @Max(Long.MAX_VALUE)
                     @RequestParam Long userId) {
        log.info("Received request for user [{}] locking", userId);
        if (userDetails.getId().equals(userId)) {
            throw new UserLockNotAllowedException();
        }
        userService.lock(userId);
    }

    /**
     * Unlocks user.
     *
     * @param userId - user id
     */
    @PreAuthorize("#oauth2.hasScope('web') and hasRole('ROLE_SUPER_ADMIN')")
    @Operation(
            description = "Unlocks user",
            summary = "Unlocks user",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Permission denied", responseCode = "403",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = ACCESS_DENIED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400")
            }
    )
    @PostMapping(value = "/unlock")
    public void unlock(@Parameter(description = "User id", example = "1", required = true)
                       @Min(VALUE_1) @Max(Long.MAX_VALUE) @RequestParam Long userId) {
        log.info("Received request for user [{}] unlocking", userId);
        userService.unlock(userId);
    }
}
