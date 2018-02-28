package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.experiment.ExperimentRequest;
import com.ecaservice.model.experiment.ExperimentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests that checks ExperimentMapper functionality {@see ExperimentMapper}.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(ExperimentMapperImpl.class)
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
        experimentRequest.setIpAddress(TestHelperUtils.IP_ADDRESS);
        Experiment experiment = experimentMapper.map(experimentRequest);
        assertThat(experiment).isNotNull();
        assertThat(experiment.getFirstName()).isEqualTo(experimentRequest.getFirstName());
        assertThat(experiment.getEmail()).isEqualTo(experimentRequest.getEmail());
        assertThat(experiment.getIpAddress()).isEqualTo(experimentRequest.getIpAddress());
        assertThat(experiment.getEvaluationMethod()).isEqualTo(experimentRequest.getEvaluationMethod());
        assertThat(experiment.getExperimentType()).isEqualTo(experimentRequest.getExperimentType());
    }
}
