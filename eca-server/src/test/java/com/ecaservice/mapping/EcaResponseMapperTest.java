package com.ecaservice.mapping;

import com.ecaservice.dto.EcaResponse;
import com.ecaservice.model.TechnicalStatus;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks EcaResponseMapper functionality {@see EcaResponseMapper}.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(EcaResponseMapperImpl.class)
public class EcaResponseMapperTest {

    @Inject
    private EcaResponseMapper ecaResponseMapper;

    @Test
    public void testMapExperimentToEcaResponse() {
        Experiment experiment = new Experiment();
        experiment.setExperimentStatus(RequestStatus.NEW);
        experiment.setUuid(UUID.randomUUID().toString());
        EcaResponse ecaResponse = ecaResponseMapper.map(experiment);
        assertThat(ecaResponse).isNotNull();
        assertThat(ecaResponse.getStatus()).isEqualTo(TechnicalStatus.SUCCESS);
        assertThat(ecaResponse.getRequestId()).isEqualTo(experiment.getUuid());
    }
}
