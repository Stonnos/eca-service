package com.ecaservice.oauth.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Cross origin requests filter component.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OauthCrossOriginFilter implements Filter {

    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    private static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    private static final String ALLOW = "*";
    private static final String METHODS = "POST, PUT, GET, OPTIONS, DELETE";
    private static final String AUTHORIZATION_CONTENT_TYPE = "Authorization, Content-Type";
    private static final int ACCESS_CONTROL_AGE_SECONDS = 3600;
    private static final String OPTIONS = "OPTIONS";

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, ALLOW);
        response.setHeader(ACCESS_CONTROL_ALLOW_METHODS, METHODS);
        response.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, AUTHORIZATION_CONTENT_TYPE);
        response.setHeader(ACCESS_CONTROL_MAX_AGE, String.valueOf(ACCESS_CONTROL_AGE_SECONDS));
        if (OPTIONS.equalsIgnoreCase(((HttpServletRequest) req).getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {
    }
}