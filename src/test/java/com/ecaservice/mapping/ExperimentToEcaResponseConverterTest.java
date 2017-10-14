package com.ecaservice.mapping;

import com.ecaservice.dto.EcaResponse;
import com.ecaservice.model.TechnicalStatus;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Roman Batygin
 */
public class ExperimentToEcaResponseConverterTest extends AbstractConverterTest {

    @Test
    public void testExperimentToEcaResponseConversionWithSuccessStatus() {
        Experiment experiment = new Experiment();
        experiment.setExperimentStatus(ExperimentStatus.NEW);
        EcaResponse ecaResponse = mapper.map(experiment, EcaResponse.class);
        System.out.println(ecaResponse);
        assertEquals(ecaResponse.getStatus(), TechnicalStatus.SUCCESS);
    }

    @Test
    public void testExperimentToEcaResponseConversionWithErrorStatus() {
        Experiment experiment = new Experiment();
        experiment.setExperimentStatus(ExperimentStatus.ERROR);
        EcaResponse ecaResponse = mapper.map(experiment, EcaResponse.class);
        assertEquals(ecaResponse.getStatus(), TechnicalStatus.ERROR);
    }
}
