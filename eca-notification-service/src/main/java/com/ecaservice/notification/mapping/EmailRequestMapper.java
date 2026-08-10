package com.ecaservice.notification.mapping;

import com.ecaservice.notification.config.MailProperties;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.entity.Email;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Implements mapping mail request to email entity.
 */
@Mapper
public interface EmailRequestMapper {

    /**
     * Maps email request to email entity.
     *
     * @param emailRequest - email request
     * @return email entity
     */
    @Mapping(target = "status", constant = "NEW")
    Email map(EmailRequest emailRequest, MailProperties mailProperties);
}
