package com.ecaservice.oauth.config;

import com.ecaservice.oauth.util.Oauth2Utils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Application properties.
 *
 * @author Roman Batygin
 */
@Data
@Validated
@ConfigurationProperties("app")
public class AppProperties {

    private static final List<String> TOKEN_IN_COOKIES_GRANT_TYPES = List.of(
            AuthorizationGrantType.PASSWORD.getValue(),
            AuthorizationGrantType.REFRESH_TOKEN.getValue(),
            Oauth2Utils.TFA_CODE.getValue()
    );

    private static final String REFRESH_TOKEN_COOKIE_PATH = "/eca-oauth/oauth2/token";

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
     * Security properties
     */
    @NotNull
    private SecurityProperties security = new SecurityProperties();

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

    /**
     * Security properties.
     */
    @Data
    public static class SecurityProperties {

        /**
         * Whitelist urls
         */
        private List<String> whitelistUrls = newArrayList();

        /**
         * Writes token in cookie?
         */
        private boolean writeTokenInCookie;

        /**
         * Refresh token cookie path
         */
        private String refreshTokenCookiePath = REFRESH_TOKEN_COOKIE_PATH;

        /**
         * Token in cookie available grant types
         */
        private List<String> tokenInCookieAvailableGrantTypes = TOKEN_IN_COOKIES_GRANT_TYPES;
    }
}
