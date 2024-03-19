package com.ecaservice.user.profile.options.client.service;

import com.ecaservice.common.web.exception.InternalServiceUnavailableException;
import com.ecaservice.user.profile.options.client.exception.UserProfileOptionsBadRequestException;
import com.ecaservice.user.profile.options.client.exception.UserProfileOptionsException;
import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * User profile options client.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileOptionsClient {

    private final UserProfileOptionsFeignClient userProfileOptionsFeignClient;

    /**
     * Gets user profile options by login.
     *
     * @param login - user login
     * @return user profile options dto
     */
    public UserProfileOptionsDto getUserProfileOptions(String login) {
        log.info("Starting to get user profile [{}] options from api", login);
        try {
            var userProfileOptions = userProfileOptionsFeignClient.getUserProfileOptions(login);
            log.info("User [{}] profile options has been fetched from api: {}", login, userProfileOptions);
            return userProfileOptions;
        } catch (FeignException.ServiceUnavailable | RetryableException ex) {
            log.error("Service unavailable error while get user [{}] profile options: {}", login, ex.getMessage());
            throw new InternalServiceUnavailableException(
                    String.format("Service unavailable error while get user [%s] profile options", login));
        } catch (FeignException.BadRequest ex) {
            log.error("Bad request error [{}] while get user [{}] profile options: {}", ex.contentUTF8(), login,
                    ex.getMessage());
            String errorMessage = String.format("Bad request error while get user [%s] profile options", login);
            throw new UserProfileOptionsBadRequestException(errorMessage);
        } catch (Exception ex) {
            log.error("Unknown error get user [{}] profile options: {}", login, ex.getMessage());
            throw new UserProfileOptionsException(
                    String.format("Unknown error while get user [%s] profile options", login));
        }
    }
}
