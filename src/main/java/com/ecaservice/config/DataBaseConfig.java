package com.ecaservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Database enabled configuration class.
 *
 * @author Roman Batygin
 */
@Data
@Component
public class DataBaseConfig {

    @Value("${database.enabled}")
    private Boolean enabled;

}
