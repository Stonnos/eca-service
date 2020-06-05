package com.ecaservice.mail.mapping;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.ecaservice.mail.config.MailConfig;
import com.ecaservice.mail.model.Email;
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
    Email map(EmailRequest emailRequest, MailConfig mailConfig);

    /**
     * Maps email entity to email response.
     *
     * @param email - email entity
     * @return email response
     */
    @Mapping(source = "uuid", target = "requestId")
    EmailResponse map(Email email);
}
