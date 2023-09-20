package com.ecaservice.data.storage.service;

import com.ecaservice.data.loader.dto.UploadInstancesResponseDto;
import com.ecaservice.data.storage.AbstractJpaTest;
import com.ecaservice.data.storage.entity.ExportInstancesObjectEntity;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.repository.ExportInstancesObjectRepository;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.data.storage.service.data.UploadInstancesObjectService;
import eca.data.file.model.InstancesModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.ecaservice.data.storage.TestHelperUtils.createExportInstancesObjectEntity;
import static com.ecaservice.data.storage.TestHelperUtils.createInstancesEntity;
import static com.ecaservice.data.storage.TestHelperUtils.loadInstancesModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ExportInstancesObjectService} functionality.
 *
 * @author Roman Batygin
 */
@Import(ExportInstancesObjectService.class)
class ExportInstancesObjectServiceTest extends AbstractJpaTest {

    private static final String MD_5_HASH = "3032e188204cb537f69fc7364f638641";

    @MockBean
    private UploadInstancesObjectService uploadInstancesObjectService;
    @MockBean
    private StorageService storageService;

    @Inject
    private InstancesRepository instancesRepository;
    @Inject
    private ExportInstancesObjectRepository exportInstancesObjectRepository;

    @Inject
    private ExportInstancesObjectService exportInstancesObjectService;

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
    void testExportNewInstances() throws IOException {
        mockUploadInstances();
        var exportInstancesResponseDto
                = exportInstancesObjectService.exportValidInstances(instancesEntity.getUuid());
        assertThat(exportInstancesResponseDto).isNotNull();
        var exportInstancesObjects = exportInstancesObjectRepository.findAll();
        assertThat(exportInstancesObjects.size()).isOne();
        var exportInstancesObjectEntity = exportInstancesObjects.iterator().next();
        assertThat(exportInstancesObjectEntity.getInstancesUuid()).isEqualTo(instancesEntity.getUuid());
        assertThat(exportInstancesObjectEntity.getExternalDataUuid()).isNotNull();
        assertThat(exportInstancesObjectEntity.getUpdatesCounter()).isEqualTo(instancesEntity.getUpdatesCounter());
    }

    @Test
    void testExportNotModifiedInstances() throws IOException {
        mockUploadInstances();
        exportInstancesObjectService.exportValidInstances(instancesEntity.getUuid());
        exportInstancesObjectService.exportValidInstances(instancesEntity.getUuid());
        var exportInstancesObjects = exportInstancesObjectRepository.findAll();
        assertThat(exportInstancesObjects.size()).isOne();
    }

    @Test
    void testExportModifiedInstances() throws IOException {
        mockUploadInstances();
        exportInstancesObjectService.exportValidInstances(instancesEntity.getUuid());
        instancesEntity.increaseUpdatesCounter();
        instancesRepository.save(instancesEntity);
        exportInstancesObjectService.exportValidInstances(instancesEntity.getUuid());
        var exportInstancesObjects = exportInstancesObjectRepository.findAll();
        assertThat(exportInstancesObjects).hasSize(2);
    }

    @Test
    void testExportExpiredInstances() throws IOException {
        mockUploadInstances();
        ExportInstancesObjectEntity expiredExportInstancesObject =
                createExportInstancesObjectEntity(instancesEntity.getUuid(), LocalDateTime.now().minusDays(1L));
        exportInstancesObjectRepository.save(expiredExportInstancesObject);
        exportInstancesObjectService.exportValidInstances(instancesEntity.getUuid());
        var exportInstancesObjects = exportInstancesObjectRepository.findAll();
        assertThat(exportInstancesObjects).hasSize(2);
    }

    private void mockUploadInstances() throws IOException {
        InstancesModel instancesModel = loadInstancesModel();
        when(storageService.getValidInstancesModel(any(InstancesEntity.class))).thenReturn(instancesModel);
        UploadInstancesResponseDto uploadInstancesResponseDto = UploadInstancesResponseDto.builder()
                .uuid(UUID.randomUUID().toString())
                .md5Hash(MD_5_HASH)
                .expireAt(LocalDateTime.now().plusDays(1L))
                .build();
        when(uploadInstancesObjectService.uploadInstances(anyString(), any(InstancesModel.class)))
                .thenReturn(uploadInstancesResponseDto);
    }
}
