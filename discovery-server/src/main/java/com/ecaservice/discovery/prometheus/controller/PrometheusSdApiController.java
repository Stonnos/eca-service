package com.ecaservice.discovery.prometheus.controller;

import com.ecaservice.discovery.prometheus.dto.PrometheusSdConfig;
import com.ecaservice.discovery.prometheus.service.PrometheusSdConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Service discovery api for prometheus.
 *
 * @author Roman Batygin
 */
@Slf4j
@RestController
@RequestMapping("/api/prometheus")
@RequiredArgsConstructor
public class PrometheusSdApiController {

    private final PrometheusSdConfigService prometheusSdConfigService;

    /**
     * Gets service discovery configs for prometheus.
     *
     * @return sd configs list
     */
    @GetMapping(value = "/sd-configs")
    public List<PrometheusSdConfig> getSdConfigs() {
        log.debug("Request get prometheus sd target configs");
        return prometheusSdConfigService.getSdConfigs();
    }
}
