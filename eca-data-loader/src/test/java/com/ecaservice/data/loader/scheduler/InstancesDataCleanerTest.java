package com.ecaservice.data.loader.scheduler;

import com.ecaservice.data.loader.AbstractJpaTest;
import com.ecaservice.data.loader.config.AppProperties;
import com.ecaservice.data.loader.entity.InstancesEntity;
import com.ecaservice.data.loader.repository.InstancesRepository;
import com.ecaservice.data.loader.service.InstancesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static com.ecaservice.data.loader.TestHelperUtils.createInstancesEntity;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link  InstancesDataCleaner} class.
 * @author Roman Batygin
 */
@Import({AppProperties.class, InstancesDataCleaner.class})
class InstancesDataCleanerTest extends AbstractJpaTest {

    @MockBean
    private InstancesService instancesService;

    @Autowired
    private InstancesRepository instancesRepository;
    @Autowired
    private InstancesDataCleaner instancesDataCleaner;

    @Override
    public void deleteAll() {
        instancesRepository.deleteAll();
    }

    @Test
    void testClearData() {
        instancesRepository.save(createInstancesEntity());
        instancesRepository.save(createInstancesEntity(LocalDateTime.now().minusDays(1L)));
        instancesDataCleaner.clearInstances();
        verify(instancesService, atLeastOnce()).deleteInstances(any(InstancesEntity.class));
    }
}
