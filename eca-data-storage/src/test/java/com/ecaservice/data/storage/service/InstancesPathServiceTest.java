package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.AbstractJpaTest;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.repository.ExportInstancesObjectRepository;
import com.ecaservice.data.storage.repository.InstancesRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static com.ecaservice.data.storage.TestHelperUtils.createExportInstancesObjectEntity;
import static com.ecaservice.data.storage.TestHelperUtils.createInstancesEntity;
import static com.ecaservice.data.storage.util.RoutePaths.INSTANCES_DETAILS_PATH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link InstancesPathService} functionality.
 *
 * @author Roman Batygin
 */
@Import(InstancesPathService.class)
class InstancesPathServiceTest extends AbstractJpaTest {

    @Autowired
    private InstancesRepository instancesRepository;
    @Autowired
    private ExportInstancesObjectRepository exportInstancesObjectRepository;

    @Autowired
    private InstancesPathService instancesPathService;

    private InstancesEntity instancesEntity;

    @Override
    public void init() {
        instancesEntity = createInstancesEntity();
        instancesRepository.save(instancesEntity);
    }

    @Override
    public void deleteAll() {
        instancesRepository.deleteAll();
        exportInstancesObjectRepository.deleteAll();
    }

    @Test
    void testGetInstancesPath() {
        var exportInstancesObjectEntity = createExportInstancesObjectEntity(instancesEntity);
        exportInstancesObjectRepository.save(exportInstancesObjectEntity);
        var routePathDto = instancesPathService.getInstancesPath(exportInstancesObjectEntity.getExternalDataUuid());
        assertThat(routePathDto).isNotNull();
        assertThat(routePathDto.getPath()).isEqualTo(String.format(INSTANCES_DETAILS_PATH, instancesEntity.getId()));
    }

    @Test
    void testGetInstancesPathNotFound() {
        var routePathDto = instancesPathService.getInstancesPath(UUID.randomUUID().toString());
        assertThat(routePathDto).isNotNull();
        assertThat(routePathDto.getPath()).isNull();
    }
}
