package com.ecaservice.user.profile.options.client.service;

import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implements simple user profile options provider.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SimpleUserProfileOptionsProvider implements UserProfileOptionsProvider {

    private final UserProfileOptionsClient userProfileOptionsClient;

    @Override
    public UserProfileOptionsDto getUserProfileOptions(String login) {
        return userProfileOptionsClient.getUserProfileOptions(login);
    }
}
