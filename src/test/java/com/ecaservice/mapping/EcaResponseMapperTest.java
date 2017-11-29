package com.ecaservice.mapping;

import com.ecaservice.dto.EcaResponse;
import com.ecaservice.model.TechnicalStatus;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests that checks EcaResponseMapper functionality
 * (see {@link EcaResponseMapper}).
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(EcaResponseMapperImpl.class)
public class EcaResponseMapperTest {

    @Autowired
    private EcaResponseMapper ecaResponseMapper;

    @Test
    public void testExperimentToEcaResponseConversionWithSuccessStatus() {
        Experiment experiment = new Experiment();
        experiment.setExperimentStatus(ExperimentStatus.NEW);
        EcaResponse ecaResponse = ecaResponseMapper.map(experiment);
        assertNotNull(ecaResponse);
        assertEquals(ecaResponse.getStatus(), TechnicalStatus.SUCCESS);
    }

    @Test
    public void testExperimentToEcaResponseConversionWithErrorStatus() {
        Experiment experiment = new Experiment();
        experiment.setExperimentStatus(ExperimentStatus.ERROR);
        EcaResponse ecaResponse = ecaResponseMapper.map(experiment);
        assertNotNull(ecaResponse);
        assertEquals(ecaResponse.getStatus(), TechnicalStatus.ERROR);
    }
}
