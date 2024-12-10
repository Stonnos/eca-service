package com.ecaservice.api.gateway.filter;

import com.ecaservice.api.gateway.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Authentication header filter. Exchanges token cookie to Authentication header.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationHeaderFilter implements GlobalFilter, Ordered {

    private static final String BEARER_FORMAT = "Bearer %s";
    private static final String ACCESS_TOKEN_COOKIE = "AccessToken";

    private final AppProperties appProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (isNotBlacklistUrl(exchange)) {
            HttpCookie accessTokenCookie = exchange.getRequest().getCookies().getFirst(ACCESS_TOKEN_COOKIE);
            if (accessTokenCookie != null) {
                ServerHttpRequest mutateRequest = exchange.getRequest()
                        .mutate()
                        .header(HttpHeaders.AUTHORIZATION, bearerToken(accessTokenCookie.getValue()))
                        .build();
                ServerWebExchange mutateExchange = exchange.mutate().request(mutateRequest).build();
                log.debug("Access token from cookie has been set to Authorization header for path [{}]",
                        exchange.getRequest().getURI().getPath());
                return chain.filter(mutateExchange);
            }
        }
        return chain.filter(exchange);
    }

    private boolean isNotBlacklistUrl(ServerWebExchange exchange) {
        String requestPath = exchange.getRequest().getURI().getPath();
        return appProperties.getAuthHeaderFilterBlacklistUrls()
                .stream()
                .noneMatch(path -> antPathMatcher.match(path, requestPath));
    }

    private String bearerToken(String token) {
        return String.format(BEARER_FORMAT, token);
    }
}
