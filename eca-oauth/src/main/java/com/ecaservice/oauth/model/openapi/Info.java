package com.ecaservice.oauth.model.openapi;

import lombok.Data;

/**
 *
 * @author Roman Batygin
 */
@Data
public class Info {
    private String title;
    private String description;
    private String termsOfService;
    private Contact contact;
    private String version;
}
