package com.ecaservice.oauth.validation;

import com.ecaservice.oauth.service.UserService;
import com.ecaservice.oauth.validation.annotations.UserPassword;
import com.ecaservice.user.model.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * User password validator.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class UserPasswordValidator implements ConstraintValidator<UserPassword, String> {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isBlank(password)) {
            return false;
        }
        UserDetailsImpl userDetails = userService.getCurrentUser();
        return passwordEncoder.matches(password.trim(), userDetails.getPassword());
    }
}
