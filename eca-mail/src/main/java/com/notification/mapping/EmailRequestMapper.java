package com.notification.mapping;

import com.ecaservice.notification.dto.EmailRequest;
import com.notification.model.Email;
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
    Email map(EmailRequest emailRequest);
}
