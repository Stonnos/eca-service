package com.ecaservice.core.redelivery.test.service;

import com.ecaservice.core.redelivery.annotation.Retry;
import com.ecaservice.core.redelivery.annotation.Retryable;
import com.ecaservice.core.redelivery.test.model.TestRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Retryable
@Service
public class TestServiceA {

    @Retry("REQUEST_1")
    public void method(TestRequest testRequest) {
        log.info("Call method with test request [{}]", testRequest);
    }
}
