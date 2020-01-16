package com.ecaservice.web.controller;

import com.ecaservice.web.config.WebConfig;
import com.ecaservice.web.dto.WebConfigDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Eca-web controller.
 */
@RestController
@RequiredArgsConstructor
public class EcaWebController {

    private final WebConfig webConfig;

    /**
     * Gets web application config.
     *
     * @return application config
     */
    @GetMapping(value = "/config")
    public WebConfigDto config() {
        return new WebConfigDto(webConfig.getApiUrl(), webConfig.getOauthUrl(), webConfig.getClientId(),
                webConfig.getSecret());
    }
}
