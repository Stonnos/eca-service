package com.ecaservice.mapping;

import com.ecaservice.TestDataHelper;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.experiment.ExperimentRequest;
import com.ecaservice.model.experiment.ExperimentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests that checks ExperimentMapper functionality
 * (see {@link ExperimentMapper}).
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ExperimentMapperImpl.class)
public class ExperimentMapperTest {

    @Autowired
    private ExperimentMapper experimentMapper;

    @Test
    public void testMapExperiment() {
        ExperimentRequest experimentRequest = new ExperimentRequest();
        experimentRequest.setFirstName("Roman");
        experimentRequest.setFirstName("mail@mail.ru");
        experimentRequest.setEvaluationMethod(EvaluationMethod.TRAINING_DATA);
        experimentRequest.setExperimentType(ExperimentType.KNN);
        experimentRequest.setIpAddress(TestDataHelper.IP_ADDRESS);

        Experiment experiment = experimentMapper.map(experimentRequest);

        assertNotNull(experiment);
        assertEquals(experiment.getFirstName(), experimentRequest.getFirstName());
        assertEquals(experiment.getEmail(), experimentRequest.getEmail());
        assertEquals(experiment.getIpAddress(), experimentRequest.getIpAddress());
        assertEquals(experiment.getEvaluationMethod(), experimentRequest.getEvaluationMethod());
        assertEquals(experiment.getExperimentType(), experimentRequest.getExperimentType());
    }
}
