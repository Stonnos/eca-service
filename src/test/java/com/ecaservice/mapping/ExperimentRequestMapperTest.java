package com.ecaservice.mapping;

import com.ecaservice.TestDataHelper;
import com.ecaservice.dto.ExperimentRequestDto;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.experiment.ExperimentRequest;
import com.ecaservice.model.experiment.ExperimentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import weka.core.Instances;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests that checks ExperimentRequestMapper functionality
 * (see {@link ExperimentRequestMapper}).
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ExperimentRequestMapperImpl.class)
public class ExperimentRequestMapperTest {

    @Autowired
    private ExperimentRequestMapper experimentRequestMapper;

    private Instances instances;

    @Before
    public void setUp() {
        instances = TestDataHelper.generateInstances(TestDataHelper.NUM_INSTANCES, TestDataHelper.NUM_ATTRIBUTES);
    }

    @Test
    public void testMapExperimentRequest() {
        ExperimentRequestDto experimentRequestDto = new ExperimentRequestDto();
        experimentRequestDto.setFirstName("Roman");
        experimentRequestDto.setEmail("mail@mail.ru");
        experimentRequestDto.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        experimentRequestDto.setExperimentType(ExperimentType.KNN);
        experimentRequestDto.setData(instances);

        ExperimentRequest experimentRequest = experimentRequestMapper.map(experimentRequestDto);
        assertNotNull(experimentRequest);
        assertEquals(experimentRequest.getFirstName(), experimentRequestDto.getFirstName());
        assertEquals(experimentRequest.getEmail(), experimentRequestDto.getEmail());
        assertEquals(experimentRequest.getEvaluationMethod(), experimentRequestDto.getEvaluationMethod());
        assertEquals(experimentRequest.getExperimentType(), experimentRequestDto.getExperimentType());
        assertNotNull(experimentRequest.getData());
        assertEquals(experimentRequest.getData().relationName(), experimentRequestDto.getData().relationName());
    }
}
