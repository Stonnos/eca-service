package com.ecaservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * User dto model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
public class UserDto {

    /**
     * User login
     */
    private String login;
}
