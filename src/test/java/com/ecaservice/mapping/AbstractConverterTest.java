package com.ecaservice.mapping;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Abstract class for mapper tests.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {OrikaBeanMapper.class,
        InstancesToInstancesInfoConverter.class,
        ClassifierToInputOptionsMapConverter.class,
        EvaluationRequestToEvaluationLogConverter.class,
        EvaluationRequestConverter.class})
public abstract class AbstractConverterTest {

    @Autowired
    protected OrikaBeanMapper mapper;

}
