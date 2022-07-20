package com.ecaservice.server.service.push;

import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.ExperimentMapper;
import com.ecaservice.server.mapping.ExperimentMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
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

import static com.ecaservice.server.TestHelperUtils.createExperiment;
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
@Import({DateTimeConverter.class, ExperimentMapperImpl.class, InstancesInfoMapperImpl.class})
class WebPushServiceTest {

    @Inject
    private ExperimentMapper experimentMapper;
    @Mock
    private WebPushClient webPushClient;

    private WebPushService webPushService;

    @BeforeEach
    void init() {
        webPushService = new WebPushService(experimentMapper, webPushClient);
    }

    @Test
    void testSendPush() {
        var experiment = createExperiment(UUID.randomUUID().toString());
        var experimentDto = experimentMapper.map(experiment);
        webPushService.sendWebPush(experiment);
        verify(webPushClient, atLeastOnce()).push(experimentDto);
    }
}
