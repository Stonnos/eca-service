package com.ecaservice.server.service.experiment.visitor;

import com.ecaservice.server.event.model.mail.ErrorExperimentEmailEvent;
import com.ecaservice.server.event.model.mail.FinishedExperimentEmailEvent;
import com.ecaservice.server.event.model.mail.InProgressExperimentEmailEvent;
import com.ecaservice.server.event.model.mail.NewExperimentEmailEvent;
import com.ecaservice.server.event.model.mail.TimeoutExperimentEmailEvent;
import com.ecaservice.server.model.entity.RequestStatus;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.UUID;
import java.util.stream.Stream;

import static com.ecaservice.server.TestHelperUtils.createExperiment;

/**
 * Experiment email event test data provider.
 *
 * @author Roman Batygin
 */
public class ExperimentEmailEventTestDataProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        return Stream.of(
                Arguments.of(
                        createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW),
                        NewExperimentEmailEvent.class
                ),
                Arguments.of(
                        createExperiment(UUID.randomUUID().toString(), RequestStatus.IN_PROGRESS),
                        InProgressExperimentEmailEvent.class
                ),
                Arguments.of(
                        createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED),
                        FinishedExperimentEmailEvent.class
                ),
                Arguments.of(
                        createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR),
                        ErrorExperimentEmailEvent.class
                ),
                Arguments.of(
                        createExperiment(UUID.randomUUID().toString(), RequestStatus.TIMEOUT),
                        TimeoutExperimentEmailEvent.class
                )
        );
    }

}
