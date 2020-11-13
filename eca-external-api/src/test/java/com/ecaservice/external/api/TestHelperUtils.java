package com.ecaservice.external.api;

import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.InputStream;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final String TEST_REQUEST_JSON = "test-request.json";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Creates evaluation request dto.
     *
     * @return evaluation request dto
     */
    @SneakyThrows
    public static EvaluationRequestDto createEvaluationRequestDto() {
        @Cleanup InputStream inputStream =
                TestHelperUtils.class.getClassLoader().getResourceAsStream(TEST_REQUEST_JSON);
        return OBJECT_MAPPER.readValue(inputStream, EvaluationRequestDto.class);
    }
}
