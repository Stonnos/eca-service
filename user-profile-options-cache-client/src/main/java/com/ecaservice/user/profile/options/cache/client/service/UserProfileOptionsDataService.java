package com.ecaservice.user.profile.options.cache.client.service;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.user.profile.options.cache.client.entity.UserProfileOptionsDataEntity;
import com.ecaservice.user.profile.options.cache.client.repository.UserProfileOptionsDataRepository;
import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.LocalDateTime;

import static com.ecaservice.common.web.util.JsonUtils.toJson;

/**
 * User profile options data service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserProfileOptionsDataService {

    private final UserProfileOptionsDataRepository userProfileOptionsDataRepository;

    /**
     * Creates or update user profile options data.
     *
     * @param userProfileOptionsDto - user profile options dto
     */
    @Locked(lockName = "createOrUpdateUserProfileOptions", key = "#userProfileOptionsDto.user")
    public void createOrUpdateUserProfileOptions(@Valid UserProfileOptionsDto userProfileOptionsDto) {
        log.info("Starting to create or update user [{}] profile options: {}", userProfileOptionsDto.getUser(),
                userProfileOptionsDto);
        var userProfileOptionsData = userProfileOptionsDataRepository.findByUser(userProfileOptionsDto.getUser());
        if (userProfileOptionsData != null &&
                userProfileOptionsDto.getVersion() < userProfileOptionsData.getVersion()) {
            throw new IllegalStateException(String.format(
                    "Can't update user [%s] profile options. Version to update [%d] is less than version in db [%d]",
                    userProfileOptionsDto.getUser(), userProfileOptionsDto.getVersion(),
                    userProfileOptionsData.getVersion()));
        }
        if (userProfileOptionsData == null) {
            log.info("User [{}] profile options not found. Starting to create user [{}] profile options: {}",
                    userProfileOptionsDto.getUser(), userProfileOptionsDto.getUser(), userProfileOptionsDto);
            createUserProfileOptionsData(userProfileOptionsDto);
        } else {
            userProfileOptionsData.setVersion(userProfileOptionsDto.getVersion());
            userProfileOptionsData.setOptionsJson(toJson(userProfileOptionsDto));
            userProfileOptionsData.setUpdated(LocalDateTime.now());
            userProfileOptionsDataRepository.save(userProfileOptionsData);
            log.info("User [{}] profile options has been updated: {}", userProfileOptionsDto.getUser(),
                    userProfileOptionsDto);
        }
    }

    private void createUserProfileOptionsData(UserProfileOptionsDto userProfileOptionsDto) {
        var userProfileOptionsData = new UserProfileOptionsDataEntity();
        userProfileOptionsData.setUser(userProfileOptionsDto.getUser());
        userProfileOptionsData.setVersion(userProfileOptionsDto.getVersion());
        userProfileOptionsData.setOptionsJson(toJson(userProfileOptionsDto));
        userProfileOptionsData.setCreated(LocalDateTime.now());
        userProfileOptionsData.setUpdated(LocalDateTime.now());
        userProfileOptionsDataRepository.save(userProfileOptionsData);
        log.info("User [{}] profile options has been created: {}", userProfileOptionsDto.getUser(),
                userProfileOptionsDto);
    }
}
