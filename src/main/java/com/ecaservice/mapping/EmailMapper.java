package com.ecaservice.mapping;

import com.ecaservice.model.Mail;
import com.ecaservice.model.entity.Email;
import org.mapstruct.Mapper;

/**
 * Email mapper.
 * @author Roman Batygin
 */
@Mapper
public abstract class EmailMapper {

    /**
     * Maps mail to email persistence entity.
     * @param mail {@link Mail} object
     * @return {@link Email} object
     */
    public abstract Email map(Mail mail);
}
