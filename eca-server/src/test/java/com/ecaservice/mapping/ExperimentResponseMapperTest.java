package com.ecaservice.mapping;

import com.ecaservice.base.model.ExperimentResponse;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks EcaResponseMapper functionality {@see EcaResponseMapper}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(ExperimentResponseMapperImpl.class)
class ExperimentResponseMapperTest {

    @Inject
    private ExperimentResponseMapper experimentResponseMapper;

    @Test
    void testMapExperimentToEcaResponse() {
        Experiment experiment = new Experiment();
        experiment.setRequestStatus(RequestStatus.NEW);
        experiment.setRequestId(UUID.randomUUID().toString());
        ExperimentResponse experimentResponse = experimentResponseMapper.map(experiment);
        assertThat(experimentResponse).isNotNull();
        assertThat(experimentResponse.getStatus()).isEqualTo(TechnicalStatus.SUCCESS);
        assertThat(experimentResponse.getRequestId()).isEqualTo(experiment.getRequestId());
    }
}
