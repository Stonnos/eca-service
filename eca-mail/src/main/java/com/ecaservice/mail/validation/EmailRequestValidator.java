package com.ecaservice.mail.validation;

import com.ecaservice.mail.model.RegexEntity;
import com.ecaservice.mail.model.TemplateEntity;
import com.ecaservice.mail.model.TemplateParameterEntity;
import com.ecaservice.mail.repository.TemplateRepository;
import com.ecaservice.mail.validation.annotations.ValidEmailRequest;
import com.ecaservice.notification.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Collections;
import java.util.Optional;

/**
 * Email request validator.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class EmailRequestValidator implements ConstraintValidator<ValidEmailRequest, EmailRequest> {

    private static final String VARIABLES_NODE_FORMAT = "variables[%s]";
    private static final String TEMPLATE_CODE_PROPERTY = "templateCode";
    private static final String EMAIL_TEMPLATE_PARAMETER_NOT_SPECIFIED = "{email.template.parameter.not.specified}";
    private static final String EMAIL_TEMPLATE_PARAMETER_INVALID_PATTERN = "{email.template.parameter.invalid.pattern}";
    private static final String EMAIL_TEMPLATE_INVALID_CODE = "{email.template.invalid.code}";

    private final TemplateRepository templateRepository;

    @Override
    public boolean isValid(EmailRequest emailRequest, ConstraintValidatorContext context) {
        var optionalTemplateEntity = templateRepository.findByCode(emailRequest.getTemplateCode());
        if (optionalTemplateEntity.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(EMAIL_TEMPLATE_INVALID_CODE)
                    .addPropertyNode(TEMPLATE_CODE_PROPERTY)
                    .addConstraintViolation();
            return false;
        } else {
            return isValidTemplateParams(emailRequest, optionalTemplateEntity.get(), context);
        }
    }

    private boolean isValidTemplateParams(EmailRequest emailRequest, TemplateEntity templateEntity,
                                          ConstraintValidatorContext context) {
        boolean valid = true;
        var variables = Optional.ofNullable(emailRequest.getVariables()).orElse(Collections.emptyMap());
        if (!CollectionUtils.isEmpty(templateEntity.getParameters())) {
            //validate all variables
            for (TemplateParameterEntity templateParameter : templateEntity.getParameters()) {
                String code = templateParameter.getParameterName();
                if (!variables.containsKey(code)) {
                    addParameterNotSpecified(context, code);
                    valid = false;
                } else {
                    String value = variables.get(code);
                    if (invalidPattern(value, templateParameter)) {
                        addInvalidPattern(context, code);
                        valid = false;
                    }
                }
            }
        }
        return valid;
    }

    private boolean invalidPattern(String value, TemplateParameterEntity templateParameter) {
        RegexEntity regexEntity = templateParameter.getRegexEntity();
        return StringUtils.isNotEmpty(value) && regexEntity != null && StringUtils.isNotEmpty(regexEntity.getRegex()) &&
                !value.matches(regexEntity.getRegex());
    }

    private void addParameterNotSpecified(ConstraintValidatorContext context, String parameter) {
        buildConstraintViolationWithTemplate(context, EMAIL_TEMPLATE_PARAMETER_NOT_SPECIFIED, parameter);
    }

    private void addInvalidPattern(ConstraintValidatorContext context, String parameter) {
        buildConstraintViolationWithTemplate(context, EMAIL_TEMPLATE_PARAMETER_INVALID_PATTERN, parameter);
    }

    private void buildConstraintViolationWithTemplate(ConstraintValidatorContext context, String template,
                                                      String parameter) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(template)
                .addPropertyNode(String.format(VARIABLES_NODE_FORMAT, parameter))
                .addConstraintViolation();
    }
}
