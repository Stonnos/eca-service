package com.ecaservice.web.push.config.ws;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Web socket configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableWebSocketMessageBroker
@EnableConfigurationProperties(QueueConfig.class)
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private static final String WS_ENDPOINT = "/socket";
    private static final String ALLOWED_ORIGINS = "*";

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(WS_ENDPOINT).setAllowedOrigins(ALLOWED_ORIGINS);
    }
}
