package com.ecaservice.user.profile.options.client.service;

import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * User profile options feign client.
 *
 * @author Roman Batygin
 */
@FeignClient(value = "eca-oauth", path = "/api/internal/user/options")
public interface UserProfileOptionsFeignClient {

    /**
     * Gets user profile options by login.
     *
     * @param login - user login
     * @return user profile options dto
     */
    @GetMapping(value = "/details")
    UserProfileOptionsDto getUserProfileOptions(@RequestParam String login);
}
