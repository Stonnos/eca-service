package com.ecaservice.data.storage.service;

import com.ecaservice.data.loader.dto.UploadInstancesResponseDto;
import com.ecaservice.data.storage.AbstractJpaTest;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.repository.ExportInstancesObjectRepository;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.data.storage.service.data.UploadInstancesObjectService;
import eca.data.file.model.InstancesModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.util.UUID;

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

    @Autowired
    private InstancesRepository instancesRepository;
    @Autowired
    private ExportInstancesObjectRepository exportInstancesObjectRepository;

    @Autowired
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

    private void mockUploadInstances() throws IOException {
        InstancesModel instancesModel = loadInstancesModel();
        when(storageService.getValidInstancesModel(any(InstancesEntity.class))).thenReturn(instancesModel);
        UploadInstancesResponseDto uploadInstancesResponseDto =
                new UploadInstancesResponseDto(UUID.randomUUID().toString(), MD_5_HASH);
        when(uploadInstancesObjectService.uploadInstances(anyString(), any(InstancesModel.class)))
                .thenReturn(uploadInstancesResponseDto);
    }
}
