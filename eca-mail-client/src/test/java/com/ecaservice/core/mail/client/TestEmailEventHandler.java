package com.ecaservice.core.mail.client;

import com.ecaservice.core.mail.client.event.listener.handler.AbstractEmailEventHandler;

/**
 * Test email event handler.
 *
 * @author Roman Batygin
 */
public class TestEmailEventHandler extends AbstractEmailEventHandler<TestEmailEvent> {

    public TestEmailEventHandler() {
        super(TestEmailEvent.class, "test_template_code");
    }
}
