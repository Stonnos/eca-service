package com.ecaservice.web.push.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * Application properties.
 *
 * @author Roman Batygin
 */
@Data
@Validated
@ConfigurationProperties("app")
public class AppProperties {

    /**
     * Maximum page size for paging requests
     */
    @NotNull
    private Integer maxPageSize;
}
