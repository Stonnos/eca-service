package com.ecaservice.oauth.validation;

import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.validation.annotations.UserEmail;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Existing user email validation.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class UserEmailValidator implements ConstraintValidator<UserEmail, String> {

    private final UserEntityRepository userEntityRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        return StringUtils.isNotBlank(email) && userEntityRepository.existsByEmail(email.trim());
    }
}