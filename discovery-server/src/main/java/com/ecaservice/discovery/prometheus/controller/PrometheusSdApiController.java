package com.ecaservice.discovery.prometheus.controller;

import com.ecaservice.discovery.prometheus.dto.PrometheusSdConfig;
import com.ecaservice.discovery.prometheus.service.PrometheusSdConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
@Tag(name = "Service discovery api for prometheus")
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
    @Operation(
            description = "Gets service discovery configs for prometheus",
            summary = "Gets service discovery configs for prometheus",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "PrometheusSdConfigsResponse",
                                                    ref = "#/components/examples/PrometheusSdConfigsResponse"
                                            )
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = PrometheusSdConfig.class))
                            )
                    )
            }
    )
    @GetMapping(value = "/sd-configs")
    public List<PrometheusSdConfig> getSdConfigs() {
        log.debug("Request get prometheus sd target configs");
        return prometheusSdConfigService.getSdConfigs();
    }
}
