package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.web.dto.ExperimentDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.UUID;

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
    public void testMapExperimentRequest() throws Exception {
        ExperimentRequest experimentRequest = TestHelperUtils.createExperimentRequest();
        Experiment experiment = experimentMapper.map(experimentRequest);
        assertThat(experiment).isNotNull();
        assertThat(experiment.getFirstName()).isEqualTo(experimentRequest.getFirstName());
        assertThat(experiment.getEmail()).isEqualTo(experimentRequest.getEmail());
        assertThat(experiment.getEvaluationMethod()).isEqualTo(experimentRequest.getEvaluationMethod());
        assertThat(experiment.getExperimentType()).isEqualTo(experimentRequest.getExperimentType());
    }

    @Test
    public void testMapExperiment() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experiment.setStartDate(LocalDateTime.now().plusHours(1L));
        experiment.setEndDate(experiment.getStartDate().minusMinutes(1L));
        experiment.setSentDate(experiment.getEndDate().plusMinutes(1L));
        ExperimentDto experimentDto = experimentMapper.map(experiment);
        assertThat(experimentDto).isNotNull();
        assertThat(experimentDto.getFirstName()).isEqualTo(experiment.getFirstName());
        assertThat(experimentDto.getEmail()).isEqualTo(experiment.getEmail());
        assertThat(experimentDto.getCreationDate()).isEqualTo(experiment.getCreationDate());
        assertThat(experimentDto.getStartDate()).isEqualTo(experiment.getStartDate());
        assertThat(experimentDto.getEndDate()).isEqualTo(experiment.getEndDate());
        assertThat(experimentDto.getSentDate()).isEqualTo(experiment.getSentDate());
        assertThat(experimentDto.getDeletedDate()).isEqualTo(experiment.getDeletedDate());
        assertThat(experimentDto.getEvaluationMethod()).isEqualTo(experiment.getEvaluationMethod().name());
        assertThat(experimentDto.getExperimentStatus()).isEqualTo(experiment.getExperimentStatus().name());
        assertThat(experimentDto.getExperimentType()).isEqualTo(experiment.getExperimentType().name());
        assertThat(experimentDto.getTrainingDataAbsolutePath()).isEqualTo(experiment.getTrainingDataAbsolutePath());
        assertThat(experimentDto.getExperimentAbsolutePath()).isEqualTo(experiment.getExperimentAbsolutePath());
        assertThat(experimentDto.getUuid()).isEqualTo(experiment.getUuid());
    }
}
