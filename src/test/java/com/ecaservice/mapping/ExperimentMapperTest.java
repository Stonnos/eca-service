package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.model.entity.Experiment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests that checks ExperimentMapper functionality {@see ExperimentMapper}.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(ExperimentMapperImpl.class)
public class ExperimentMapperTest {

    @Inject
    private ExperimentMapper experimentMapper;

    @Test
    public void testMapExperiment() throws Exception {
        ExperimentRequest experimentRequest = TestHelperUtils.createExperimentRequest();
        Experiment experiment = experimentMapper.map(experimentRequest);
        assertThat(experiment).isNotNull();
        assertThat(experiment.getFirstName()).isEqualTo(experimentRequest.getFirstName());
        assertThat(experiment.getEmail()).isEqualTo(experimentRequest.getEmail());
        assertThat(experiment.getEvaluationMethod()).isEqualTo(experimentRequest.getEvaluationMethod());
        assertThat(experiment.getExperimentType()).isEqualTo(experimentRequest.getExperimentType());
    }
}
