package com.ecaservice.core.mail.client.mapping;

import com.ecaservice.core.mail.client.entity.EmailRequestEntity;
import com.ecaservice.notification.dto.EmailRequest;
import org.mapstruct.Mapper;

/**
 * Email request mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface EmailRequestMapper {

    /**
     * Maps email request to entity model.
     *
     * @param emailRequest - email request
     * @return email request entity
     */
    EmailRequestEntity map(EmailRequest emailRequest);
}
