package com.ecaservice.data.loader.service;

import com.ecaservice.common.web.exception.InternalServiceUnavailableException;
import com.ecaservice.common.web.exception.InvalidFileException;
import com.ecaservice.data.loader.AbstractJpaTest;
import com.ecaservice.data.loader.entity.InstancesEntity;
import com.ecaservice.data.loader.repository.InstancesRepository;
import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.model.UploadObject;
import com.ecaservice.s3.client.minio.service.MinioStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.file.model.InstancesModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;

import javax.inject.Inject;
import java.io.IOException;

import static com.ecaservice.data.loader.TestHelperUtils.createInstancesMockMultipartFile;
import static com.ecaservice.data.loader.TestHelperUtils.loadInstances;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

/**
 * Unit tests for checking {@link UploadInstancesService} functionality.
 *
 * @author Roman Batygin
 */
@Import({UploadInstancesService.class, ObjectMapper.class})
class UploadInstancesServiceTest extends AbstractJpaTest {

    @MockBean
    private MinioStorageService minioStorageService;

    @Inject
    private InstancesRepository instancesRepository;

    @Inject
    private UploadInstancesService uploadInstancesService;

    @Override
    public void deleteAll() {
        instancesRepository.deleteAll();
    }

    @Test
    void testUploadInstances() throws IOException {
        MockMultipartFile multipartFile = createInstancesMockMultipartFile();
        InstancesModel instancesModel = loadInstances();
        var uploadInstancesResponseDto = uploadInstancesService.uploadInstances(multipartFile);
        InstancesEntity actual = instancesRepository.findByUuid(uploadInstancesResponseDto.getUuid()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getCreated()).isNotNull();
        assertThat(actual.getUuid()).isNotNull();
        assertThat(actual.getClassName()).isEqualTo(instancesModel.getClassName());
        assertThat(actual.getRelationName()).isEqualTo(instancesModel.getRelationName());
        assertThat(actual.getNumInstances()).isEqualTo(instancesModel.getInstances().size());
        assertThat(actual.getNumAttributes()).isEqualTo(instancesModel.getAttributes().size());
        assertThat(actual.getObjectPath()).isNotNull();
        assertThat(actual.getMd5Hash()).isNotNull();
        assertNumClasses(instancesModel, actual);
    }

    @Test
    void testUploadInstancesWithInvalidFileExtension() {
        MockMultipartFile multipartFile = createInstancesMockMultipartFile("file.txt");
        assertThrows(InvalidFileException.class, () -> uploadInstancesService.uploadInstances(multipartFile));
    }

    @Test
    void testUploadInstancesWithObjectStorageUnavailableException() {
        doThrow(ObjectStorageException.class).when(minioStorageService).uploadObject(any(UploadObject.class));
        MockMultipartFile multipartFile = createInstancesMockMultipartFile();
        assertThrows(InternalServiceUnavailableException.class,
                () -> uploadInstancesService.uploadInstances(multipartFile));
    }

    private void assertNumClasses(InstancesModel instancesModel, InstancesEntity actual) {
        var classAttribute = instancesModel.getAttributes()
                .stream()
                .filter(attributeModel -> attributeModel.getName().equals(instancesModel.getClassName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Class attribute [%s] not found in instances [%s] attributes set",
                                instancesModel.getClassName(), instancesModel.getRelationName())));
        assertThat(actual.getNumClasses()).isEqualTo(classAttribute.getValues().size());
    }
}
