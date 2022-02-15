package com.ecaservice.core.redelivery;

import com.ecaservice.core.redelivery.entity.RetryRequest;
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
}
