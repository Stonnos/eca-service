package com.ecaservice.service.auth;

import com.ecaservice.config.feign.FeignOauthConfiguration;
import com.ecaservice.web.dto.model.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Auth server feign client.
 *
 * @author Roman Batygin
 */
@FeignClient(value = "eca-oauth", path = "/users", configuration = FeignOauthConfiguration.class)
public interface UsersClient {

    /**
     * Gets current authenticated user info.
     *
     * @return user info
     */
    @GetMapping(value = "/user-info")
    UserDto getUserInfo();
}
