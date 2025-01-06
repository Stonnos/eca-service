package com.ecaservice.core.transactional.outbox.test.service;

import com.ecaservice.core.transactional.outbox.annotation.OutboxSender;
import com.ecaservice.core.transactional.outbox.annotation.TransactionalOutbox;
import com.ecaservice.core.transactional.outbox.test.model.TestMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@TransactionalOutbox
@Service
public class TestService {

    @OutboxSender("REQUEST_1")
    public void method(TestMessage testMessage) {
        log.info("Call method with test message [{}]", testMessage);
    }
}
