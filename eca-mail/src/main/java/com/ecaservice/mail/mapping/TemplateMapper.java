package com.ecaservice.mail.mapping;

import com.ecaservice.mail.model.TemplateEntity;
import com.ecaservice.mail.model.TemplateParameterEntity;
import com.ecaservice.web.dto.model.EmailTemplateDto;
import com.ecaservice.web.dto.model.EmailTemplateParameterDto;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Email template mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface TemplateMapper {

    /**
     * Maps email template entity to dto model.
     *
     * @param templateEntity - email template entity
     * @return email template dto
     */
    EmailTemplateDto map(TemplateEntity templateEntity);

    /**
     * Maps email templates entities to dto list.
     *
     * @param templates - email templates
     * @return email templates dto list
     */
    List<EmailTemplateDto> mapTemplates(List<TemplateEntity> templates);

    /**
     * Maps email template parameter to dto model.
     *
     * @param templateParameterEntity - email template parameter
     * @return email template parameter dto
     */
    EmailTemplateParameterDto map(TemplateParameterEntity templateParameterEntity);

    /**
     * Maps email template parameters to dto models.
     *
     * @param templateParameters - email template parameters
     * @return email template parameters dto list
     */
    List<EmailTemplateParameterDto> map(List<TemplateParameterEntity> templateParameters);
}
