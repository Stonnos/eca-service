package com.ecaservice.server.mapping;

import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentProgressEntity;
import com.ecaservice.web.dto.model.ExperimentProgressDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createExperiment;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for checking {@link ExperimentProgressMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(ExperimentProgressMapperImpl.class)
class ExperimentProgressMapperTest {

    private static final int PROGRESS_VALUE = 64;

    @Autowired
    private ExperimentProgressMapper experimentProgressMapper;

    @Test
    void testMapExperimentProgress() {
        ExperimentProgressEntity experimentProgressEntity = new ExperimentProgressEntity();
        experimentProgressEntity.setFinished(false);
        experimentProgressEntity.setProgress(PROGRESS_VALUE);
        Experiment experiment = createExperiment(UUID.randomUUID().toString());
        experiment.setStartDate(LocalDateTime.now());
        experimentProgressEntity.setExperiment(experiment);
        ExperimentProgressDto experimentProgressDto = experimentProgressMapper.map(experimentProgressEntity);
        assertThat(experimentProgressDto).isNotNull();
        assertThat(experimentProgressDto.getProgress()).isEqualTo(experimentProgressEntity.getProgress());
        assertThat(experimentProgressDto.isFinished()).isEqualTo(experimentProgressEntity.isFinished());
        assertThat(experimentProgressDto.getEstimatedTimeLeft()).isNotNull();
    }
}
