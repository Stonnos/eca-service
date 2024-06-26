package com.ecaservice.core.redelivery.service;

import com.ecaservice.core.redelivery.test.service.TestServiceA;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit tests for {@link RedeliveryValidationService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({RedeliveryValidationService.class, TestServiceA.class})
class RedeliveryValidationTest {

    @Autowired
    private RedeliveryValidationService redeliveryValidationService;

    /***
     * Unit test for success validation.
     */
    @Test
    void testValidation() {
        redeliveryValidationService.validate();
    }
}
