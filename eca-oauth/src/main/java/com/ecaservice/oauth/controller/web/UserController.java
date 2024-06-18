package com.ecaservice.oauth.controller.web;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.dto.UpdateUserInfoDto;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.entity.UserPhoto;
import com.ecaservice.oauth.event.model.UserCreatedEmailEvent;
import com.ecaservice.oauth.event.model.UserLockedEmailEvent;
import com.ecaservice.oauth.event.model.UserUnLockedEmailEvent;
import com.ecaservice.oauth.mapping.UserMapper;
import com.ecaservice.oauth.repository.UserPhotoRepository;
import com.ecaservice.oauth.service.PasswordService;
import com.ecaservice.oauth.service.UserService;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.UserDictionaryDto;
import com.ecaservice.web.dto.model.UserDto;
import com.ecaservice.web.dto.model.UsersDictionaryPageDto;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import java.security.Principal;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;
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
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserPhotoRepository userPhotoRepository;

    /**
     * Gets current authenticated user info.
     *
     * @param principal - principal object
     * @return users list
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Gets current authenticated user info",
            summary = "Gets current authenticated user info",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "UserDtoResponse",
                                                    ref = "#/components/examples/UserDtoResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    )
            }
    )
    @GetMapping(value = "/user-info")
    public UserDto getUserDetails(Principal principal) {
        log.debug("Request get current user [{}]", principal.getName());
        return userService.getUserDetails(principal.getName());
    }

    /**
     * Enable/disable tfa for current authenticated user.
     *
     * @param principal - principal object
     * @param enabled   - tfa enabled?
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
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
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "InvalidTfaOperationResponse",
                                                    ref = "#/components/examples/InvalidTfaOperationResponse"
                                            ),
                                    }
                            ))
            }
    )
    @PostMapping(value = "/tfa")
    public void tfa(Principal principal,
                    @Parameter(description = "Tfa enabled flag", required = true) @RequestParam boolean enabled) {
        if (enabled) {
            userService.enableTfa(principal.getName());
        } else {
            userService.disableTfa(principal.getName());
        }
    }

    /**
     * Finds all users with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return users page
     */
    @PreAuthorize("hasAuthority('SCOPE_web') and hasRole('ROLE_SUPER_ADMIN')")
    @Operation(
            description = "Finds users with specified options",
            summary = "Finds users with specified options",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "PageRequest",
                                    ref = "#/components/examples/PageRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "UsersPageResponse",
                                                    ref = "#/components/examples/UsersPageResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = UsersPageDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "BadPageRequestResponse",
                                                    ref = "#/components/examples/BadPageRequestResponse"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    ),
                    @ApiResponse(description = "Permission denied", responseCode = "403",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "AccessDeniedResponse",
                                                    ref = "#/components/examples/AccessDeniedResponse"
                                            ),
                                    }
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
     * Finds users dictionary with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return users dictionary page
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Finds users dictionary with specified options such as filter, sorting and paging",
            summary = "Finds users dictionary with specified options such as filter, sorting and paging",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "PageRequest",
                                    ref = "#/components/examples/PageRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "UsersDictionaryPageResponse",
                                                    ref = "#/components/examples/UsersDictionaryPageResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = UsersDictionaryPageDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "BadPageRequestResponse",
                                                    ref = "#/components/examples/BadPageRequestResponse"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/users-dictionary")
    public PageDto<UserDictionaryDto> getUsersDictionary(@Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received users dictionary page request: {}", pageRequestDto);
        return userService.getUsersDictionaryPage(pageRequestDto);
    }

    /**
     * Creates new user.
     *
     * @param createUserDto - create user dto
     */
    @PreAuthorize("hasAuthority('SCOPE_web') and hasRole('ROLE_SUPER_ADMIN')")
    @Operation(
            description = "Creates new user",
            summary = "Creates new user",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "CreateUserRequest",
                                    ref = "#/components/examples/CreateUserRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "UserDtoResponse",
                                                    ref = "#/components/examples/UserDtoResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Permission denied", responseCode = "403",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "AccessDeniedResponse",
                                                    ref = "#/components/examples/AccessDeniedResponse"
                                            ),
                                    }
                            )),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "UniqueLoginErrorResponse",
                                                    ref = "#/components/examples/UniqueLoginErrorResponse"
                                            ),
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
        applicationEventPublisher.publishEvent(new UserCreatedEmailEvent(this, userEntity, password));
        return userMapper.map(userEntity);
    }

    /**
     * Updates info for current authenticated user.
     *
     * @param principal         - principal object
     * @param updateUserInfoDto - user info dto
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Updates info for current authenticated user",
            summary = "Updates info for current authenticated user",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "UpdateUserInfoRequest",
                                    ref = "#/components/examples/UpdateUserInfoRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "UpdateUserInfoBadRequestResponse",
                                                    ref = "#/components/examples/UpdateUserInfoBadRequestResponse"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PutMapping(value = "/update-info")
    public void updateUserInfo(Principal principal,
                               @Valid @RequestBody UpdateUserInfoDto updateUserInfoDto) {
        log.info("Received request to update user [{}] info", principal.getName());
        userService.updateUserInfo(principal.getName(), updateUserInfoDto);
    }

    /**
     * Uploads photo for current authenticated user.
     *
     * @param principal - principal object
     * @param file      - user photo file
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
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
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400")
            }
    )
    @PostMapping(value = "/upload-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadPhoto(Principal principal,
                            @Parameter(description = "Photo file", required = true) @RequestParam MultipartFile file) {
        log.info("Uploads photo [{}] for user [{}]", file.getOriginalFilename(), principal.getName());
        userService.updatePhoto(principal.getName(), file);
    }

    /**
     * Downloads user photo.
     *
     * @param id - photo id
     * @return user photo as byte array
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
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
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "DataNotFoundResponse",
                                                    ref = "#/components/examples/DataNotFoundResponse"
                                            ),
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
        log.info("Request to download user photo [{}]", id);
        UserPhoto userPhoto = userPhotoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UserPhoto.class, id));
        log.info("User photo [{}] has been fetched from db", id);
        return buildAttachmentResponse(userPhoto.getPhoto(), userPhoto.getFileName());
    }

    /**
     * Deletes photo for current authenticated user
     *
     * @param principal - principal object
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
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
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "DataNotFoundResponse",
                                                    ref = "#/components/examples/DataNotFoundResponse"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @DeleteMapping(value = "/delete-photo")
    public void deletePhoto(Principal principal) {
        log.info("Deletes photo for user [{}]", principal.getName());
        userService.deletePhoto(principal.getName());
    }

    /**
     * Locks user.
     *
     * @param userId - user id
     */
    @PreAuthorize("hasAuthority('SCOPE_web') and hasRole('ROLE_SUPER_ADMIN')")
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
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Permission denied", responseCode = "403",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "AccessDeniedResponse",
                                                    ref = "#/components/examples/AccessDeniedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400")
            }
    )
    @PostMapping(value = "/lock")
    public void lock(@Parameter(description = "User id", example = "1", required = true)
                     @Min(VALUE_1) @Max(Long.MAX_VALUE)
                     @RequestParam Long userId) {
        log.info("Received request for user [{}] locking", userId);
        var userEntity = userService.lock(userId);
        applicationEventPublisher.publishEvent(new UserLockedEmailEvent(this, userEntity));
    }

    /**
     * Unlocks user.
     *
     * @param userId - user id
     */
    @PreAuthorize("hasAuthority('SCOPE_web') and hasRole('ROLE_SUPER_ADMIN')")
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
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Permission denied", responseCode = "403",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "AccessDeniedResponse",
                                                    ref = "#/components/examples/AccessDeniedResponse"
                                            ),
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
        var userEntity = userService.unlock(userId);
        applicationEventPublisher.publishEvent(new UserUnLockedEmailEvent(this, userEntity));
    }
}
