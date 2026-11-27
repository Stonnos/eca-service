package com.ecaservice.oauth.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link BearerTokenAuthenticationEntryPointTest} class.
 *
 * @author Roman Batygin
 */
class BearerTokenAuthenticationEntryPointTest {

    private BearerTokenAuthenticationEntryPoint entryPoint;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        entryPoint = new BearerTokenAuthenticationEntryPoint();
        request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/resource");
        response = new MockHttpServletResponse();
    }

    @Test
    void testCommenceWithOAuth2AuthenticationException() throws Exception {
        OAuth2Error error = new OAuth2Error(
                "invalid_token",
                "The access token expired",
                "https://ietf.org"
        );
        OAuth2AuthenticationException authException = new OAuth2AuthenticationException(error);

        entryPoint.commence(request, response, authException);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(response.getContentAsString())
                .contains("\"error\":\"invalid_token\"")
                .contains("\"error_description\":\"The access token expired\"")
                .contains("\"error_uri\":\"https://ietf.org\"");
    }

    @Test
    void testCommenceWithGenericAuthenticationException() throws Exception {
        AuthenticationException authException = new BadCredentialsException("Bad credentials");

        entryPoint.commence(request, response, authException);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(response.getContentType()).isNull();
        assertThat(response.getContentAsString()).isEmpty();
    }
}
