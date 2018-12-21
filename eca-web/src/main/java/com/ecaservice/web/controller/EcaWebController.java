package com.ecaservice.web.controller;

import com.google.common.base.Charsets;
import org.apache.commons.io.IOUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Eca-web controller.
 */
@Controller
public class EcaWebController {

    private static final String CONFIG_LOCATION_TEMPLATE = "/static/assets/configs/config-%s.json";
    private static final String DEFAULT_CONFIG_LOCATION_TEMPLATE = "/static/assets/configs/config.json";
    private static final String FORWARD = "forward:/";

    private final Environment environment;

    /**
     * Constructor with spring dependency injection.
     *
     * @param environment - environment bean
     */
    @Inject
    public EcaWebController(Environment environment) {
        this.environment = environment;
    }

    /**
     * Passes URL to Angular router.
     *
     * @return forward url
     */
    @RequestMapping({"/login", "/dashboard/**"})
    public String index() {
        return FORWARD;
    }

    /**
     * Loads config json string.
     *
     * @return config json string
     */
    @GetMapping(value = "/config", produces = "application/json")
    @ResponseBody
    public String config() throws IOException {
        String profile = getActiveProfile();
        String path = StringUtils.isEmpty(profile) ? DEFAULT_CONFIG_LOCATION_TEMPLATE :
                String.format(CONFIG_LOCATION_TEMPLATE, profile);
        return loadJsonConfig(path);
    }

    private String getActiveProfile() {
        return environment.getActiveProfiles().length > 0 ? environment.getActiveProfiles()[0] : null;
    }

    private String loadJsonConfig(String path) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(path);
        return IOUtils.toString(classPathResource.getInputStream(), Charsets.UTF_8);
    }
}
