package com.ecaservice.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web MVC configuration for passing URL to Angular router.
 *
 * @author Roman Batygin
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private static final String FORWARD = "forward:/";

    private static final List<String> URL_PATHS =
            List.of("/login", "/dashboard/**", "/create-reset-password-request", "/reset-password/**",
                    "/change-password/**", "/access-denied", "/change-email/**");

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        URL_PATHS.forEach(url -> registry.addViewController(url).setViewName(FORWARD));
    }
}
