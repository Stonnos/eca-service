package com.ecaservice.core.redelivery.service;

import com.ecaservice.core.redelivery.test.service.TestServiceA;
import com.ecaservice.core.redelivery.test.service.TestServiceB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link RedeliveryValidationService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({TestServiceA.class, TestServiceB.class})
class RedeliveryValidationFailedTest {

    @Inject
    private ApplicationContext applicationContext;

    private RedeliveryValidationService redeliveryValidationService;

    @BeforeEach
    void init() {
        redeliveryValidationService = new RedeliveryValidationService(applicationContext);
    }

    /***
     * Unit test for failed validation.
     */
    @Test
    void testValidation() {
        assertThrows(IllegalArgumentException.class, () -> redeliveryValidationService.validate());
    }
}
