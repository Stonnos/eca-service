package com.ecaservice.auto.test.mapping;

import com.ecaservice.auto.test.dto.EmailTestStepDto;
import com.ecaservice.auto.test.entity.autotest.EmailTestStepEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Email test step mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class EmailTestStepMapper extends BaseTestStepMapper<EmailTestStepEntity, EmailTestStepDto> {

    public EmailTestStepMapper() {
        super(EmailTestStepEntity.class);
    }

    /**
     * Maps email test step entity to dto model.
     *
     * @param emailTestStepEntity -  email test step entity
     * @return email test step dto
     */
    @Mapping(source = "emailType.description", target = "emailTypeDescription")
    public abstract EmailTestStepDto map(EmailTestStepEntity emailTestStepEntity);
}
