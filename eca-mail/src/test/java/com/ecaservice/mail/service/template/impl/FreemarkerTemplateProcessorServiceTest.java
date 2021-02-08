package com.ecaservice.mail.service.template.impl;

import com.ecaservice.mail.exception.TemplateProcessingException;
import freemarker.template.Configuration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link FreemarkerTemplateProcessorService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
class FreemarkerTemplateProcessorServiceTest {

    private static final String TEST_TEMPLATE = "testTemplate";

    @Mock
    private Configuration configuration;
    @InjectMocks
    private FreemarkerTemplateProcessorService templateProcessorService;

    @Test
    void testThrowTemplateProcessingException() throws IOException {
        when(configuration.getTemplate(TEST_TEMPLATE)).thenThrow(new IOException());
        assertThrows(TemplateProcessingException.class, () -> templateProcessorService.process(TEST_TEMPLATE,
                Collections.emptyMap()));
    }
}
