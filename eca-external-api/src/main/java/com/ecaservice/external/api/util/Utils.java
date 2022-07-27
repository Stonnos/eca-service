package com.ecaservice.external.api.util;

import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.external.api.dto.ResponseCode;
import com.ecaservice.external.api.dto.ResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Converts classifier options to json string.
     *
     * @param classifierOptions - classifier options
     * @return classifier options as json
     */
    public static String toJson(ClassifierOptions classifierOptions) {
        try {
            return OBJECT_MAPPER.writeValueAsString(classifierOptions);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Builds response with specified fields.
     *
     * @param responseCode - response code
     * @param <T>          - payload generic type
     * @return response object
     */
    public static <T> ResponseDto<T> buildResponse(ResponseCode responseCode) {
        return buildResponse(responseCode, null);
    }

    /**
     * Builds response with specified fields.
     *
     * @param responseCode - response code
     * @param payload      - payload object
     * @param <T>          - payload generic type
     * @return response object
     */
    public static <T> ResponseDto<T> buildResponse(ResponseCode responseCode, T payload) {
        return ResponseDto.<T>builder()
                .responseCode(responseCode)
                .errorDescription(responseCode.getDescription())
                .payload(payload)
                .build();
    }
}
