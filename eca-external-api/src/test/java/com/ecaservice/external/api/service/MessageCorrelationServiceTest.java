package com.ecaservice.external.api.service;

import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.MonoSink;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link MessageCorrelationService} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({ExternalApiConfig.class, MessageCorrelationService.class})
class MessageCorrelationServiceTest {

    @Inject
    private MessageCorrelationService messageCorrelationService;

    @Mock
    private MonoSink<EvaluationResponseDto> sink;

    @Test
    void testPopMethod() {
        String correlationId = UUID.randomUUID().toString();
        messageCorrelationService.push(correlationId, sink);
        Optional<MonoSink<EvaluationResponseDto>> evaluationResponseDtoOptional =
                messageCorrelationService.pop(correlationId);
        assertThat(evaluationResponseDtoOptional).isPresent();
        assertThat(messageCorrelationService.pop(correlationId)).isNotPresent();
    }
}
