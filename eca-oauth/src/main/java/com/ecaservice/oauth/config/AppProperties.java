package com.ecaservice.oauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

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
     * Web application external base url
     */
    @NotEmpty
    private String webExternalBaseUrl;

    /**
     * Valid user photo file extensions
     */
    private List<String> validUserPhotoFileExtensions;

    /**
     * Reset password properties
     */
    @NotNull
    private TokenValidityProperties resetPassword = new TokenValidityProperties();

    /**
     * Change password properties
     */
    @NotNull
    private TokenValidityProperties changePassword = new TokenValidityProperties();

    /**
     * Change email properties
     */
    @NotNull
    private TokenValidityProperties changeEmail = new TokenValidityProperties();

    /**
     * Token validity properties
     */
    @Data
    public static class TokenValidityProperties {

        public static final int DEFAULT_CODE_LENGTH = 6;

        /**
         * Confirmation code length
         */
        private Integer confirmationCodeLength = DEFAULT_CODE_LENGTH;

        /**
         * Token validity in minutes
         */
        @NotNull
        private Long validityMinutes;

        /**
         * Token url
         */
        private String url;
    }
}
