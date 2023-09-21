package com.ecaservice.core.mail.client;

import com.ecaservice.core.mail.client.event.listener.handler.AbstractEmailEventHandler;

/**
 * Test email event handler.
 *
 * @author Roman Batygin
 */
public class TestEmailEventHandler extends AbstractEmailEventHandler<TestEmailEvent> {

    public TestEmailEventHandler() {
        super(TestEmailEvent.class);
    }

    @Override
    public String getReceiver(TestEmailEvent emailEvent) {
        return "test@mail.ru";
    }

    @Override
    public String getTemplateCode(TestEmailEvent emailEvent) {
        return "test_template_code";
    }
}
