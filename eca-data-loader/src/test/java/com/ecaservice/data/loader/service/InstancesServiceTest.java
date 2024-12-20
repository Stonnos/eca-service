package com.ecaservice.data.loader.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.data.loader.AbstractJpaTest;
import com.ecaservice.data.loader.entity.InstancesEntity;
import com.ecaservice.data.loader.mapping.InstancesMapperImpl;
import com.ecaservice.data.loader.repository.InstancesRepository;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static com.ecaservice.data.loader.TestHelperUtils.createInstancesEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for checking {@link InstancesService} functionality.
 *
 * @author Roman Batygin
 */
@Import({InstancesService.class, InstancesMapperImpl.class})
class InstancesServiceTest extends AbstractJpaTest {

    @MockBean
    private ObjectStorageService objectStorageService;

    @Autowired
    private InstancesRepository instancesRepository;

    @Autowired
    private InstancesService instancesService;

    private InstancesEntity instancesEntity;

    @Override
    public void init() {
        var instances = createInstancesEntity();
        instancesEntity = instancesRepository.save(instances);
    }

    @Override
    public void deleteAll() {
        instancesRepository.deleteAll();
    }

    @Test
    void testGetMetaInfo() {
        var instancesMetaInfo = instancesService.getMetaInfo(instancesEntity.getUuid());
        assertThat(instancesMetaInfo.getNumInstances()).isEqualTo(instancesEntity.getNumInstances());
        assertThat(instancesMetaInfo.getNumAttributes()).isEqualTo(instancesEntity.getNumAttributes());
        assertThat(instancesMetaInfo.getNumClasses()).isEqualTo(instancesEntity.getNumClasses());
        assertThat(instancesMetaInfo.getRelationName()).isEqualTo(instancesEntity.getRelationName());
        assertThat(instancesMetaInfo.getClassName()).isEqualTo(instancesEntity.getClassName());
        assertThat(instancesMetaInfo.getUuid()).isEqualTo(instancesEntity.getUuid());
        assertThat(instancesMetaInfo.getMd5Hash()).isEqualTo(instancesEntity.getMd5Hash());
        assertThat(instancesMetaInfo.getObjectPath()).isEqualTo(instancesEntity.getObjectPath());
        assertThat(instancesMetaInfo.getAttributes()).hasSameSizeAs(instancesEntity.getAttributes());
    }

    @Test
    void testGetMetaInfoShouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> instancesService.getMetaInfo("123"));
    }
}
