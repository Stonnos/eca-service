package com.ecaservice.user.profile.options.client.service;

import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;

/**
 * User profile options provider.
 *
 * @author Roman Batygin
 */
public interface UserProfileOptionsProvider {

    /**
     * Gets user profile options by login.
     *
     * @param login - user login
     * @return user profile options dto
     */
    UserProfileOptionsDto getUserProfileOptions(String login);
}
