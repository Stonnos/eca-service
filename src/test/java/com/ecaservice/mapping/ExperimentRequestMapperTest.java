package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.ExperimentRequestDto;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.experiment.ExperimentRequest;
import com.ecaservice.model.experiment.ExperimentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import weka.core.Instances;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks ExperimentRequestMapper functionality {@see ExperimentRequestMapper}.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ExperimentRequestMapperTest {

    @Inject
    private ExperimentRequestMapper experimentRequestMapper;

    private Instances instances;

    @Before
    public void setUp() throws Exception {
        instances = TestHelperUtils.loadInstances();
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
        assertThat(experimentRequest).isNotNull();
        assertThat(experimentRequest.getFirstName()).isEqualTo(experimentRequestDto.getFirstName());
        assertThat(experimentRequest.getEmail()).isEqualTo(experimentRequestDto.getEmail());
        assertThat(experimentRequest.getEvaluationMethod()).isEqualTo(experimentRequestDto.getEvaluationMethod());
        assertThat(experimentRequest.getExperimentType()).isEqualTo(experimentRequestDto.getExperimentType());
        assertThat(experimentRequest.getData()).isNotNull();
        assertThat(experimentRequest.getData().relationName()).isEqualTo(experimentRequestDto.getData().relationName());
    }
}
