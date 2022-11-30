package com.ecaservice.server.service.experiment.visitor;

import com.ecaservice.server.model.entity.Experiment;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ExperimentEmailEventVisitor} class.
 *
 * @author Roman Batygin
 */
class ExperimentEmailEventVisitorTest {

    private ExperimentEmailEventVisitor experimentEmailEventVisitor = new ExperimentEmailEventVisitor();

    @ParameterizedTest
    @ArgumentsSource(ExperimentEmailEventTestDataProvider.class)
    void testExperimentEmailEvent(Experiment experiment, Class<?> expectedEventClazz) {
        var event = experiment.getRequestStatus().handle(experimentEmailEventVisitor, experiment);
        assertThat(event).isInstanceOf(expectedEventClazz);
    }
}
