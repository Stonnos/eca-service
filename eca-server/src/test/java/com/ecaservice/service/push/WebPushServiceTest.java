package com.ecaservice.service.push;

import com.ecaservice.config.NotificationConfig;
import com.ecaservice.mapping.DateTimeConverter;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.mapping.ExperimentMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.UUID;

import static com.ecaservice.TestHelperUtils.createExperiment;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link WebPushService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({DateTimeConverter.class, ExperimentMapperImpl.class, NotificationConfig.class})
class WebPushServiceTest {

    @Inject
    private NotificationConfig notificationConfig;
    @Inject
    private ExperimentMapper experimentMapper;
    @Mock
    private WebPushClient webPushClient;

    private WebPushService webPushService;

    @BeforeEach
    void init() {
        webPushService = new WebPushService(notificationConfig, experimentMapper, webPushClient);
    }

    @Test
    void testSendPush() {
        var experiment = createExperiment(UUID.randomUUID().toString());
        var experimentDto = experimentMapper.map(experiment);
        webPushService.sendWebPush(experiment);
        verify(webPushClient, atLeastOnce()).push(experimentDto);
    }
}
