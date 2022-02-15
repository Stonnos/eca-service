package com.ecaservice.core.redelivery;

import com.ecaservice.core.redelivery.entity.RetryRequest;
import com.ecaservice.core.redelivery.test.model.TestRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final String REQUEST_TYPE = "REQUEST_TYPE";
    private static final String REQUEST = "request";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Creates retry request.
     *
     * @return retry request
     */
    public static RetryRequest createRetryRequest() {
        var retryRequest = new RetryRequest();
        retryRequest.setRequestType(REQUEST_TYPE);
        retryRequest.setRequest(REQUEST);
        retryRequest.setMaxRetries(0);
        retryRequest.setCreatedAt(LocalDateTime.now());
        return retryRequest;
    }

    /**
     * Creates retry request.
     *
     * @param requestType - request type
     * @param testRequest - test request
     * @param retries     - retries
     * @param maxRetries  - max retries
     * @return retry request
     */
    @SneakyThrows
    public static RetryRequest createRetryRequest(String requestType, TestRequest testRequest, int retries,
                                                  int maxRetries) {
        var retryRequest = new RetryRequest();
        retryRequest.setRequestType(requestType);
        retryRequest.setRequest(OBJECT_MAPPER.writeValueAsString(testRequest));
        retryRequest.setRetries(retries);
        retryRequest.setMaxRetries(maxRetries);
        retryRequest.setCreatedAt(LocalDateTime.now());
        return retryRequest;
    }
}
