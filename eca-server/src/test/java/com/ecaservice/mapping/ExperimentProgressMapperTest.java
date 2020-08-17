package com.ecaservice.mapping;

import com.ecaservice.model.entity.ExperimentProgressEntity;
import com.ecaservice.web.dto.model.ExperimentProgressDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for checking {@link ExperimentProgressMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(ExperimentProgressMapperImpl.class)
class ExperimentProgressMapperTest {

    private static final int PROGRESS_VALUE = 100;

    @Inject
    private ExperimentProgressMapper experimentProgressMapper;

    @Test
    void testMapExperimentProgress() {
        ExperimentProgressEntity experimentProgressEntity = new ExperimentProgressEntity();
        experimentProgressEntity.setFinished(true);
        experimentProgressEntity.setProgress(PROGRESS_VALUE);
        ExperimentProgressDto experimentProgressDto = experimentProgressMapper.map(experimentProgressEntity);
        assertThat(experimentProgressDto).isNotNull();
        assertThat(experimentProgressDto.getProgress()).isEqualTo(experimentProgressEntity.getProgress());
        assertThat(experimentProgressDto.isFinished()).isEqualTo(experimentProgressEntity.isFinished());
    }
}
