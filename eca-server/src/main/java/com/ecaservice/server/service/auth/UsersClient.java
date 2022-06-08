package com.ecaservice.server.service.auth;

import com.ecaservice.server.config.feign.FeignOauthConfiguration;
import com.ecaservice.user.dto.UserInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Auth server feign client.
 *
 * @author Roman Batygin
 */
@FeignClient(value = "eca-oauth", path = "/api/internal/users", configuration = FeignOauthConfiguration.class)
public interface UsersClient {

    /**
     * Gets user info by login.
     *
     * @param login - user login
     * @return user info dto
     */
    @GetMapping(value = "/user-info")
    UserInfoDto getUserInfo(@RequestParam String login);
}
