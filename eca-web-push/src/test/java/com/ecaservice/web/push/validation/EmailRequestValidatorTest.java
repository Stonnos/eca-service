package com.ecaservice.web.push.validation;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.web.push.entity.TemplateEntity;
import com.ecaservice.web.push.entity.TemplateParameterEntity;
import com.ecaservice.web.push.repository.TemplateRepository;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.ecaservice.web.push.TestHelperUtils.createEmailRequest;
import static com.ecaservice.web.push.TestHelperUtils.createRegex;
import static com.ecaservice.web.push.TestHelperUtils.createTemplateEntity;
import static com.ecaservice.web.push.TestHelperUtils.createTemplateParameterEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EmailRequestValidator} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class EmailRequestValidatorTest {

    private static final String PARAM_2 = "param2";
    private static final String PARAM_1 = "param1";
    private static final String TEST_REGEX = "[0-9]+";

    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext customizableContext;

    @Mock
    private TemplateRepository templateRepository;

    private EmailRequestValidator emailRequestValidator;

    @BeforeEach
    void init() {
        emailRequestValidator = new EmailRequestValidator(templateRepository);
    }

    @Test
    void testValidateTemplateWithoutParameters() {
        TemplateEntity templateEntity = createTemplateEntity();
        when(templateRepository.findByCode(templateEntity.getCode())).thenReturn(Optional.of(templateEntity));
        EmailRequest emailRequest = createEmailRequest();
        emailRequest.setTemplateCode(templateEntity.getCode());
        assertThat(emailRequestValidator.isValid(emailRequest, context)).isTrue();
    }

    @Test
    void testValidateTemplateWithParams() {
        TemplateEntity templateEntity = createTemplateEntity();
        templateEntity.getParameters().add(createTemplateParameterEntity(PARAM_1));
        templateEntity.getParameters().add(createTemplateParameterEntity(PARAM_2));
        EmailRequest emailRequest = createEmailRequest();
        emailRequest.getVariables().put(PARAM_1, PARAM_1);
        emailRequest.getVariables().put(PARAM_2, PARAM_2);
        emailRequest.setTemplateCode(templateEntity.getCode());
        when(templateRepository.findByCode(templateEntity.getCode())).thenReturn(Optional.of(templateEntity));
        assertThat(emailRequestValidator.isValid(emailRequest, context)).isTrue();
    }

    @Test
    void testValidateTemplateWithMissingRequiredParams() {
        TemplateEntity templateEntity = createTemplateEntity();
        templateEntity.getParameters().add(createTemplateParameterEntity(PARAM_1));
        templateEntity.getParameters().add(createTemplateParameterEntity(PARAM_2));
        EmailRequest emailRequest = createEmailRequest();
        emailRequest.getVariables().put(PARAM_1, PARAM_1);
        emailRequest.setTemplateCode(templateEntity.getCode());
        testInvalidRequest(templateEntity, emailRequest);
    }

    @Test
    void testValidateTemplateWithInvalidParamValue() {
        TemplateEntity templateEntity = createTemplateEntity();
        templateEntity.getParameters().add(createTemplateParameterEntity(PARAM_1));
        TemplateParameterEntity templateParameterEntity = createTemplateParameterEntity(PARAM_2);
        templateParameterEntity.setRegexEntity(createRegex(TEST_REGEX));
        templateEntity.getParameters().add(templateParameterEntity);
        EmailRequest emailRequest = createEmailRequest();
        emailRequest.getVariables().put(PARAM_1, PARAM_1);
        emailRequest.getVariables().put(PARAM_2, PARAM_2);
        emailRequest.setTemplateCode(templateEntity.getCode());
        testInvalidRequest(templateEntity, emailRequest);
    }

    @Test
    void testValidateInvalidTemplateCode() {
        EmailRequest emailRequest = createEmailRequest();
        when(templateRepository.findByCode(emailRequest.getTemplateCode())).thenReturn(Optional.empty());
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addPropertyNode(anyString())).thenReturn(customizableContext);
        assertThat(emailRequestValidator.isValid(emailRequest, context)).isFalse();
    }

    private void testInvalidRequest(TemplateEntity templateEntity, EmailRequest emailRequest) {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addPropertyNode(anyString())).thenReturn(customizableContext);
        when(templateRepository.findByCode(templateEntity.getCode())).thenReturn(Optional.of(templateEntity));
        assertThat(emailRequestValidator.isValid(emailRequest, context)).isFalse();
    }
}
