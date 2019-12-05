package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.report.model.ExperimentBean;
import com.ecaservice.web.dto.model.ExperimentDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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

    private static final String TRAINING_DATA_ABSOLUTE_PATH = "/home/data.xls";
    private static final String EXPERIMENT_ABSOLUTE_PATH = "/home/experiment.model";
    private static final String DATA_XLS = "data.xls";
    private static final String EXPERIMENT_MODEL = "experiment.model";

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
    public void testMapToExperimentDto() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experiment.setStartDate(LocalDateTime.now().plusHours(1L));
        experiment.setEndDate(experiment.getStartDate().minusMinutes(1L));
        experiment.setSentDate(experiment.getEndDate().plusMinutes(1L));
        experiment.setDeletedDate(experiment.getEndDate().plusMinutes(1L));
        experiment.setTrainingDataAbsolutePath(TRAINING_DATA_ABSOLUTE_PATH);
        experiment.setExperimentAbsolutePath(EXPERIMENT_ABSOLUTE_PATH);
        ExperimentDto experimentDto = experimentMapper.map(experiment);
        assertThat(experimentDto).isNotNull();
        assertThat(experimentDto.getFirstName()).isEqualTo(experiment.getFirstName());
        assertThat(experimentDto.getEmail()).isEqualTo(experiment.getEmail());
        assertThat(experimentDto.getCreationDate()).isEqualTo(experiment.getCreationDate());
        assertThat(experimentDto.getStartDate()).isEqualTo(experiment.getStartDate());
        assertThat(experimentDto.getEndDate()).isEqualTo(experiment.getEndDate());
        assertThat(experimentDto.getSentDate()).isEqualTo(experiment.getSentDate());
        assertThat(experimentDto.getDeletedDate()).isEqualTo(experiment.getDeletedDate());
        assertThat(experimentDto.getEvaluationMethod().getDescription()).isEqualTo(
                experiment.getEvaluationMethod().getDescription());
        assertThat(experimentDto.getEvaluationMethod().getValue()).isEqualTo(
                experiment.getEvaluationMethod().name());
        assertThat(experimentDto.getExperimentStatus().getDescription()).isEqualTo(
                experiment.getExperimentStatus().getDescription());
        assertThat(experimentDto.getExperimentStatus().getValue()).isEqualTo(
                experiment.getExperimentStatus().name());
        assertThat(experimentDto.getExperimentType().getDescription()).isEqualTo(
                experiment.getExperimentType().getDescription());
        assertThat(experimentDto.getExperimentType().getValue()).isEqualTo(
                experiment.getExperimentType().name());
        assertThat(experimentDto.getTrainingDataAbsolutePath()).isEqualTo(DATA_XLS);
        assertThat(experimentDto.getExperimentAbsolutePath()).isEqualTo(EXPERIMENT_MODEL);
        assertThat(experimentDto.getUuid()).isEqualTo(experiment.getUuid());
    }

    @Test
    public void testMapExperimentDtoList() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        Experiment experiment1 = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        List<ExperimentDto> experimentDtoList = experimentMapper.map(Arrays.asList(experiment, experiment1));
        assertThat(experimentDtoList).isNotNull();
        assertThat(experimentDtoList).hasSize(2);
    }

    @Test
    public void testMapToExperimentBean() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experiment.setStartDate(LocalDateTime.now());
        experiment.setEndDate(LocalDateTime.now());
        experiment.setSentDate(LocalDateTime.now());
        experiment.setDeletedDate(LocalDateTime.now());
        experiment.setTrainingDataAbsolutePath(TRAINING_DATA_ABSOLUTE_PATH);
        experiment.setExperimentAbsolutePath(EXPERIMENT_ABSOLUTE_PATH);
        ExperimentBean experimentBean = experimentMapper.mapToBean(experiment);
        assertThat(experimentBean).isNotNull();
        assertThat(experimentBean.getFirstName()).isEqualTo(experiment.getFirstName());
        assertThat(experimentBean.getEmail()).isEqualTo(experiment.getEmail());
        assertThat(experimentBean.getCreationDate()).isNotNull();
        assertThat(experimentBean.getStartDate()).isNotNull();
        assertThat(experimentBean.getEndDate()).isNotNull();
        assertThat(experimentBean.getSentDate()).isNotNull();
        assertThat(experimentBean.getDeletedDate()).isNotNull();
        assertThat(experimentBean.getEvaluationMethod()).isEqualTo(experiment.getEvaluationMethod().getDescription());
        assertThat(experimentBean.getExperimentStatus()).isEqualTo(experiment.getExperimentStatus().getDescription());
        assertThat(experimentBean.getExperimentType()).isEqualTo(experiment.getExperimentType().getDescription());
        assertThat(experimentBean.getTrainingDataAbsolutePath()).isEqualTo(DATA_XLS);
        assertThat(experimentBean.getExperimentAbsolutePath()).isEqualTo(EXPERIMENT_MODEL);
        assertThat(experimentBean.getUuid()).isEqualTo(experiment.getUuid());
    }
}
