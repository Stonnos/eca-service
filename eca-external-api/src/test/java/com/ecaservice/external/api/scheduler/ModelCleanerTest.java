package com.ecaservice.external.api.scheduler;

import com.ecaservice.external.api.AbstractJpaTest;
import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.InstancesEntity;
import com.ecaservice.external.api.repository.InstancesRepository;
import com.ecaservice.external.api.service.InstancesService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.time.LocalDateTime;

import static com.ecaservice.external.api.TestHelperUtils.createInstancesEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for checking {@link ModelCleaner} functionality.
 *
 * @author Roman Batygin
 */
@Import({ExternalApiConfig.class, ModelCleaner.class})
class ModelCleanerTest extends AbstractJpaTest {

    @MockBean
    private InstancesService instancesService;

    @Inject
    private ExternalApiConfig externalApiConfig;
    @Inject
    private InstancesRepository instancesRepository;
    @Inject
    private ModelCleaner modelCleaner;

    @Override
    public void deleteAll() {
        instancesRepository.deleteAll();
    }

    @Test
    void testClearData() {
        LocalDateTime dateTime = LocalDateTime.now().minusDays(externalApiConfig.getNumberOfDaysForStorage() + 1);
        instancesRepository.save(createInstancesEntity(LocalDateTime.now()));
        instancesRepository.save(createInstancesEntity(dateTime));
        modelCleaner.clearData();
        verify(instancesService, atLeastOnce()).deleteInstances(any(InstancesEntity.class));
    }
}
