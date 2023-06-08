package com.ecaservice.common.web.util;

import com.ecaservice.common.error.model.ErrorDetails;
import com.ecaservice.common.error.model.ValidationErrorDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Validation error helper.
 *
 * @author Roman Batygin
 */
@Slf4j
@UtilityClass
public class ValidationErrorHelper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
     * Gets first error code from validation errors as enum.
     *
     * @param validationErrors - validation errors
     * @param enumClass        - enum class
     * @param <T>              - enum generic type
     * @return first validation error
     */
    public static <T extends Enum<T> & ErrorDetails> T getFirstErrorCodeAsEnum(
            List<ValidationErrorDto> validationErrors,
            Class<T> enumClass) {
        var errorCodes = Stream.of(enumClass.getEnumConstants())
                .map(ErrorDetails::getCode)
                .collect(Collectors.toList());
        var validationError = getFirstError(errorCodes, validationErrors);
        if (validationError.isEmpty()) {
            log.warn("Can't find any of error codes [{}] in validation errors list", errorCodes);
            return null;
        }
        return validationError
                .map(validationErrorDto -> fromCode(validationErrorDto.getCode(), enumClass))
                .orElse(null);
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

    /**
     * Gets validation errors list from response body.
     *
     * @param responseBody - json response body
     * @return validation errors list
     * @throws JsonProcessingException in case of json processing error
     */
    public static List<ValidationErrorDto> retrieveValidationErrors(String responseBody)
            throws JsonProcessingException {
        Assert.notNull(responseBody, "Expected not empty response body");
        return OBJECT_MAPPER.readValue(responseBody, new TypeReference<>() {
        });
    }

    private static <T extends Enum<T> & ErrorDetails> T fromCode(String code, Class<T> enumClass) {
        return Stream.of(enumClass.getEnumConstants())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}
