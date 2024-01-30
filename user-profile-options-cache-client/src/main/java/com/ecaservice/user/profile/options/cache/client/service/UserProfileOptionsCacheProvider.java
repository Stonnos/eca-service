package com.ecaservice.user.profile.options.cache.client.service;

import com.ecaservice.user.profile.options.cache.client.repository.UserProfileOptionsDataRepository;
import com.ecaservice.user.profile.options.client.service.UserProfileOptionsClient;
import com.ecaservice.user.profile.options.client.service.UserProfileOptionsProvider;
import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import static com.ecaservice.user.profile.options.cache.client.util.JsonUtils.fromJson;

/**
 * User profile options cache provider.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class UserProfileOptionsCacheProvider implements UserProfileOptionsProvider {

    private final UserProfileOptionsClient userProfileOptionsClient;
    private final UserProfileOptionsDataService userProfileOptionsDataService;
    private final UserProfileOptionsDataRepository userProfileOptionsDataRepository;

    @Override
    public UserProfileOptionsDto getUserProfileOptions(String login) {
        log.info("Starting to get user [{}] profile options from cache", login);
        UserProfileOptionsDto userProfileOptionsDto;
        var userProfileOptionsData = userProfileOptionsDataRepository.findByUser(login);
        if (userProfileOptionsData == null) {
            //Gets user profile options from rest api
            userProfileOptionsDto = userProfileOptionsClient.getUserProfileOptions(login);
            userProfileOptionsDataService.createOrUpdateUserProfileOptions(userProfileOptionsDto);
        } else {
            userProfileOptionsDto = fromJson(userProfileOptionsData.getOptionsJson(), UserProfileOptionsDto.class);
        }
        log.info("User [{}] profile options has been fetched from cache", login);
        return userProfileOptionsDto;
    }
}
