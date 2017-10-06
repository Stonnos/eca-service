package com.ecaservice.mapping;

import com.ecaservice.TestDataHelper;
import com.ecaservice.model.entity.InstancesInfo;
import org.junit.Before;
import org.junit.Test;
import weka.core.Instances;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests that checks InstancesToInstancesInfoConverter functionality
 * (see {@link InstancesToInstancesInfoConverter}).
 *
 * @author Roman Batygin
 */
public class InstancesToInstancesInfoConverterTest extends AbstractConverterTest {

    private Instances instances;

    @Before
    public void setUp() {
        instances = TestDataHelper.generate(TestDataHelper.NUM_INSTANCES, TestDataHelper.NUM_ATTRIBUTES);
    }

    @Test
    public void testMapInstances() {
        InstancesInfo instancesInfo = mapper.map(instances, InstancesInfo.class);
        assertEquals(instances.relationName(), instancesInfo.getRelationName());
        assertEquals(instances.numInstances(), instancesInfo.getNumInstances().intValue());
        assertEquals(instances.numAttributes(), instancesInfo.getNumAttributes().intValue());
        assertEquals(instances.numClasses(), instancesInfo.getNumClasses().intValue());
    }
}
