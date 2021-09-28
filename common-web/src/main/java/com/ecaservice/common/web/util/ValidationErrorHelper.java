package com.ecaservice.common.web.util;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * Validation error helper.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ValidationErrorHelper {

    /**
     * Gets first validation error with specified error codes.
     *
     * @param errorCodes       - error codes list
     * @param validationErrors - validation errors
     * @return first validation error
     */
    public static Optional<ValidationErrorDto> getFirstError(List<String> errorCodes,
                                                             List<ValidationErrorDto> validationErrors) {
        if (CollectionUtils.isEmpty(validationErrors) || CollectionUtils.isEmpty(errorCodes)) {
            return Optional.empty();
        }
        return validationErrors.stream()
                .filter(validationErrorDto -> errorCodes.contains(validationErrorDto.getCode()))
                .findFirst();
    }

    /**
     * Checks that validation errors has specified error code.
     *
     * @param errorCode        - error code
     * @param validationErrors - validation errors
     * @return {@code true} if validation errors has specified error code, otherwise {@code false}
     */
    public static boolean hasError(String errorCode, List<ValidationErrorDto> validationErrors) {
        if (CollectionUtils.isEmpty(validationErrors)) {
            return false;
        }
        return validationErrors.stream()
                .anyMatch(validationErrorDto -> validationErrorDto.getCode().equals(errorCode));
    }
}
