package com.ecaservice.oauth.validation;

import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.validation.annotations.UniqueLogin;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Unique login validation.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class UniqueLoginValidator implements ConstraintValidator<UniqueLogin, String> {

    private final UserEntityRepository userEntityRepository;

    @Override
    public boolean isValid(String login, ConstraintValidatorContext constraintValidatorContext) {
        return StringUtils.isNotBlank(login) && !userEntityRepository.existsByLogin(login.trim());
    }
}