package com.ecaservice.server.service.auth;

import com.ecaservice.feign.oauth.config.FeignClientOauth2Configuration;
import com.ecaservice.user.dto.PersonalAccessTokenInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Personal access tokens feign client.
 *
 * @author Roman Batygin
 */
@FeignClient(value = "eca-oauth", contextId = "personalAccessToken", path = "/api/internal/personal-access-token",
        configuration = FeignClientOauth2Configuration.class)
public interface PersonalAccessTokensClient {

    /**
     * Verify personal access token.
     *
     * @param token - token value
     * @return personal access token info dto
     */
    @GetMapping(value = "/verify-token")
    PersonalAccessTokenInfoDto verifyToken(@RequestParam String token);
}
