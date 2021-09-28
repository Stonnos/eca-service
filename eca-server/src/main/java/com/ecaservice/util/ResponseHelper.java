package com.ecaservice.util;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.exception.EcaServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Response helper class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ResponseHelper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Gets validation errors list from response body.
     *
     * @param responseBody - json response body
     * @return validation errors list
     */
    public static List<ValidationErrorDto> retrieveValidationErrors(String responseBody) {
        Assert.notNull(responseBody, "Expected not empty response body");
        try {
            return OBJECT_MAPPER.readValue(responseBody, new TypeReference<>() {
            });
        } catch (JsonProcessingException ex) {
            throw new EcaServiceException(ex.getMessage());
        }
    }
}
