package com.ecaservice.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for passing URL to Angular router.
 *
 * @author Roman Batygin
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private static final String FORWARD = "forward:/";
    private static final String LOGIN_URL = "/login";
    private static final String DASHBOARD_URLS = "/dashboard/**";
    private static final String FORGOT_PASSWORD_URL = "/forgot-password";
    private static final String RESET_PASSWORD_URL = "/reset-password/**";

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController(LOGIN_URL).setViewName(FORWARD);
        registry.addViewController(DASHBOARD_URLS).setViewName(FORWARD);
        registry.addViewController(FORGOT_PASSWORD_URL).setViewName(FORWARD);
        registry.addViewController(RESET_PASSWORD_URL).setViewName(FORWARD);
    }
}
