package com.ecaservice.server.mq.listener;

import com.ecaservice.server.exception.MessageAuthorizationException;
import com.ecaservice.server.service.auth.PersonalAccessTokensClient;
import com.ecaservice.user.dto.PersonalAccessTokenInfoDto;
import com.ecaservice.user.dto.PersonalAccessTokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.util.UUID;

import static com.ecaservice.server.mq.listener.MessageHeaders.AUTH_TOKEN_HEADER;
import static com.ecaservice.server.mq.listener.MessageHeaders.USER_HEADER;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AuthorizeMessagePostProcessor} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class AuthorizeMessagePostProcessorTest {

    private static final String AUTH_TOKEN = "sample-token";
    private static final String USERNAME = "test-user";
    private static final String EMPTY_TOKEN_ERROR = "Auth token must be specified!";
    private static final String INVALID_TOKEN_ERROR = "Got invalid auth token!";

    @Mock
    private PersonalAccessTokensClient personalAccessTokensClient;

    private AuthorizeMessagePostProcessor postProcessor;

    @BeforeEach
    void setUp() {
        postProcessor = new AuthorizeMessagePostProcessor(personalAccessTokensClient);
    }

    @Test
    void testPostProcessMessageWhenAuthTokenIsMissing() {
        Message message = createMessage(null);
        assertThatThrownBy(() -> postProcessor.postProcessMessage(message))
                .isInstanceOf(MessageAuthorizationException.class)
                .hasMessage(EMPTY_TOKEN_ERROR);
        verifyNoInteractions(personalAccessTokensClient);
    }

    @Test
    void testPostProcessMessageWhenAuthTokenIsEmpty() {
        Message message = createMessage(EMPTY);
        assertThatThrownBy(() -> postProcessor.postProcessMessage(message))
                .isInstanceOf(MessageAuthorizationException.class)
                .hasMessage(EMPTY_TOKEN_ERROR);
        verifyNoInteractions(personalAccessTokensClient);
    }

    @Test
    void testPostProcessMessageWhenAuthTokenIsInvalid() {
        Message message = createMessage(AUTH_TOKEN);
        PersonalAccessTokenInfoDto tokenInfo = createTokenInfo(false, null, null);
        when(personalAccessTokensClient.verifyToken(AUTH_TOKEN)).thenReturn(tokenInfo);
        assertThatThrownBy(() -> postProcessor.postProcessMessage(message))
                .isInstanceOf(MessageAuthorizationException.class)
                .hasMessage(INVALID_TOKEN_ERROR);
    }

    @Test
    void testPostProcessMessageWithUserToken() {
        Message message = createMessage(AUTH_TOKEN);
        PersonalAccessTokenInfoDto tokenInfo = createTokenInfo(true, PersonalAccessTokenType.USER_TOKEN, USERNAME);
        when(personalAccessTokensClient.verifyToken(AUTH_TOKEN)).thenReturn(tokenInfo);

        Message result = postProcessor.postProcessMessage(message);
        assertThat(result).isSameAs(message);
        assertThat((String) result.getMessageProperties().getHeader(USER_HEADER)).isEqualTo(USERNAME);
    }

    private Message createMessage(String token) {
        MessageProperties properties = new MessageProperties();
        properties.setCorrelationId(UUID.randomUUID().toString());
        if (token != null) {
            properties.setHeader(AUTH_TOKEN_HEADER, token);
        }
        return new Message(new byte[0], properties);
    }

    private PersonalAccessTokenInfoDto createTokenInfo(boolean valid, PersonalAccessTokenType type, String user) {
        PersonalAccessTokenInfoDto tokenInfo = new PersonalAccessTokenInfoDto();
        tokenInfo.setValid(valid);
        tokenInfo.setTokenType(type);
        tokenInfo.setUser(user);
        return tokenInfo;
    }
}
