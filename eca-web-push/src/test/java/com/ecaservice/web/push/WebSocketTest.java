package com.ecaservice.web.push;

import com.ecaservice.web.dto.model.push.PushRequestDto;
import com.ecaservice.web.dto.model.push.PushType;
import com.ecaservice.web.push.config.ws.QueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Web socket integration test.
 *
 * @author Roman Batygin
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application.properties")
class WebSocketTest {

    private static final String WS_URL_FORMAT = "ws://localhost:%d/socket";
    private static final int CONNECT_TIMEOUT = 10;
    private static final int POOL_TIMEOUT = 10;
    private static final String SCOPE_WEB = "SCOPE_web";
    private static final String BEARER_TOKEN_FORMAT = "Bearer %s";

    @MockBean
    private OpaqueTokenIntrospector opaqueTokenIntrospector;

    @Autowired
    private QueueConfig queueConfig;

    private WebSocketStompClient webSocketStompClient;

    private final BlockingQueue<PushRequestDto> blockingQueue = new ArrayBlockingQueue<>(1);

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() {
        mockTokenIntrospection();
        this.blockingQueue.clear();
        this.webSocketStompClient = new WebSocketStompClient(new StandardWebSocketClient());
        this.webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    void testSendStompMessage() throws ExecutionException, InterruptedException, TimeoutException {
        StompSession session = createSession();
        subscribe(session);

        PushRequestDto request = new PushRequestDto();
        request.setPushType(PushType.SYSTEM);
        request.setRequestId(UUID.randomUUID().toString());
        sendMessage(session, request);

        PushRequestDto actual = blockingQueue.poll(POOL_TIMEOUT, SECONDS);
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestId()).isEqualTo(request.getRequestId());
    }

    private void subscribe(StompSession session) {
        StompHeaders subscribeHeaders = new StompHeaders();
        subscribeHeaders.setDestination(queueConfig.getPushQueue());
        session.subscribe(subscribeHeaders, new StompSessionHandlerAdapter() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return PushRequestDto.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                log.info("Received message: headers = {}, payload = {}", headers, payload);
                blockingQueue.add((PushRequestDto) payload);
            }

            @Override
            public void handleException(StompSession session, @Nullable StompCommand command,
                                        StompHeaders headers, byte[] payload, Throwable exception) {
                log.error("Error while send message: {}", exception.getMessage(), exception);
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                log.error("Transport error while send message: {}", exception.getMessage(), exception);
            }
        });
    }

    private void sendMessage(StompSession session, PushRequestDto pushRequestDto) {
        StompHeaders headers = new StompHeaders();
        headers.setDestination(queueConfig.getPushQueue());
        session.send(headers, pushRequestDto);
    }

    private StompSession createSession() throws ExecutionException, InterruptedException, TimeoutException {
        WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
        webSocketHttpHeaders.add(HttpHeaders.AUTHORIZATION, getBearerToken());
        return webSocketStompClient
                .connectAsync(getWsPath(), webSocketHttpHeaders, new StompSessionHandlerAdapter() {
                })
                .get(CONNECT_TIMEOUT, SECONDS);
    }

    private String getWsPath() {
        return String.format(WS_URL_FORMAT, port);
    }

    private String getBearerToken() {
        return String.format(BEARER_TOKEN_FORMAT, UUID.randomUUID());
    }

    private void mockTokenIntrospection() {
        Map<String, Object> claims = newHashMap();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        claims.put(OAuth2TokenIntrospectionClaimNames.SCOPE, Collections.singletonList(SCOPE_WEB));
        authorities.add(new SimpleGrantedAuthority(SCOPE_WEB));
        var oAuth2IntrospectionAuthenticatedPrincipal =
                new OAuth2IntrospectionAuthenticatedPrincipal(claims, authorities);
        when(opaqueTokenIntrospector.introspect(anyString())).thenReturn(oAuth2IntrospectionAuthenticatedPrincipal);
    }
}
