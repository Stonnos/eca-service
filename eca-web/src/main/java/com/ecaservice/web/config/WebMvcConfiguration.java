package com.ecaservice.web.config;

import com.google.common.collect.ImmutableList;
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
            ImmutableList.of("/login", "/dashboard/**", "/forgot-password", "/reset-password/**");

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        URL_PATHS.forEach(url -> registry.addViewController(url).setViewName(FORWARD));
    }
}
