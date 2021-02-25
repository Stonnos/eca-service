package com.ecaservice.oauth.controller;

import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.entity.UserPhoto;
import com.ecaservice.oauth.event.model.UserCreatedEvent;
import com.ecaservice.oauth.mapping.UserMapper;
import com.ecaservice.oauth.repository.UserPhotoRepository;
import com.ecaservice.oauth.service.PasswordService;
import com.ecaservice.oauth.service.UserService;
import com.ecaservice.user.model.UserDetailsImpl;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.UserDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

import static com.ecaservice.oauth.util.Utils.buildAttachmentResponse;

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
    private final UserPhotoRepository userPhotoRepository;

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
        UserEntity userEntity = userService.getById(userDetails.getId());
        UserDto userDto = userMapper.map(userEntity);
        userDto.setPhotoId(userPhotoRepository.getUserPhotoId(userEntity));
        return userDto;
    }

    /**
     * Sets tfa enabled for current authenticated user.
     *
     * @param enabled - tfa enabled?
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Sets tfa enabled for current authenticated user",
            notes = "Sets tfa enabled for current authenticated user"
    )
    @PostMapping(value = "/tfa-enabled")
    public void setTfaEnabled(@AuthenticationPrincipal UserDetailsImpl userDetails,
                              @ApiParam(value = "Tfa enabled flag", required = true) @RequestParam boolean enabled) {
        userService.setTfaEnabled(userDetails.getId(), enabled);
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
        log.info("User {} has been created", userEntity.getId());
        applicationEventPublisher.publishEvent(new UserCreatedEvent(this, userEntity, password));
        return userMapper.map(userEntity);
    }

    /**
     * Uploads photo for current authenticated user.
     *
     * @param userDetails - user details
     * @param file        - user photo file
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Uploads photo for current authenticated user",
            notes = "Uploads photo for current authenticated user"
    )
    @PostMapping(value = "/upload-photo")
    public void uploadPhoto(@AuthenticationPrincipal UserDetailsImpl userDetails,
                            @ApiParam(value = "Photo file", required = true) @RequestParam MultipartFile file) {
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
    @ApiOperation(
            value = "Downloads user photo",
            notes = "Downloads user photo"
    )
    @GetMapping(value = "/photo/{id}")
    public ResponseEntity<ByteArrayResource> downloadPhoto(
            @ApiParam(value = "Photo id", required = true, example = "1") @PathVariable Long id) {
        UserPhoto userPhoto = userPhotoRepository.findById(id).orElse(null);
        if (userPhoto == null) {
            log.error("Can't find user photo with id [{}]", id);
            return ResponseEntity.notFound().build();
        }
        return buildAttachmentResponse(userPhoto.getPhoto(), userPhoto.getFileName());
    }

    /**
     * Deletes photo for current authenticated user
     *
     * @param userDetails - user details
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Deletes photo for current authenticated user",
            notes = "Deletes photo for current authenticated user"
    )
    @DeleteMapping(value = "/delete-photo")
    public void deletePhoto(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("Deletes photo for user [{}]", userDetails.getId());
        userService.deletePhoto(userDetails.getId());
    }
}
