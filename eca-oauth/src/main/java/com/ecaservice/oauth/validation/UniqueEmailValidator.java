package com.ecaservice.oauth.validation;

import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.validation.annotations.UniqueEmail;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Unique email validation.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserEntityRepository userEntityRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return StringUtils.isNotBlank(email) && !userEntityRepository.existsByEmail(email.trim());
    }
}