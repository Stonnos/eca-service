package com.ecaservice.external.api.service;

import com.ecaservice.external.api.AbstractJpaTest;
import com.ecaservice.external.api.entity.InstancesEntity;
import com.ecaservice.external.api.exception.DataNotFoundException;
import com.ecaservice.external.api.exception.ProcessFileException;
import com.ecaservice.external.api.repository.InstancesRepository;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import eca.data.file.FileDataLoader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import weka.core.Instances;

import javax.inject.Inject;
import java.io.IOException;
import java.net.UnknownHostException;

import static com.ecaservice.external.api.TestHelperUtils.createInstancesMockMultipartFile;
import static com.ecaservice.external.api.TestHelperUtils.loadInstances;
import static com.ecaservice.external.api.util.Constants.DATA_URL_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link InstancesService} functionality.
 *
 * @author Roman Batygin
 */
@Import(InstancesService.class)
class InstancesServiceTest extends AbstractJpaTest {

    private static final String TRAIN_DATA_MODEL_PATH_FORMAT = "train-data-%s.model";
    private static final String TEST_DATA_URL = "data://test-data";
    private static final String HTTP_TEST_DATA_URL = "http://test/data.csv";

    @MockBean
    private FileDataLoader fileDataLoader;

    @MockBean
    private ObjectStorageService objectStorageService;

    @Inject
    private InstancesRepository instancesRepository;

    @Inject
    private InstancesService instancesService;

    @Override
    public void deleteAll() {
        instancesRepository.deleteAll();
    }

    @Test
    void testUploadInstances() throws IOException {
        MockMultipartFile multipartFile = createInstancesMockMultipartFile();
        InstancesEntity instancesEntity = instancesService.uploadInstances(multipartFile);
        InstancesEntity actual = instancesRepository.findById(instancesEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getCreationDate()).isNotNull();
        assertThat(actual.getUuid()).isNotNull();
        assertThat(actual.getDataPath()).isNotNull();
        String expectedDataPath = String.format(TRAIN_DATA_MODEL_PATH_FORMAT, instancesEntity.getUuid());
        assertThat(actual.getDataPath()).isEqualTo(expectedDataPath);
    }

    @Test
    void testDataNotFoundForInternalStorage() {
        assertThrows(DataNotFoundException.class, () -> instancesService.loadInstances(TEST_DATA_URL));
    }

    @Test
    void testDataNotFoundForExternalUrl() throws Exception {
        when(fileDataLoader.loadInstances()).thenThrow(new UnknownHostException());
        assertThrows(DataNotFoundException.class, () -> instancesService.loadInstances(HTTP_TEST_DATA_URL));
    }

    @Test
    void testLoadInstancesFromStorage() throws Exception {
        MockMultipartFile multipartFile = createInstancesMockMultipartFile();
        InstancesEntity instancesEntity = instancesService.uploadInstances(multipartFile);
        internalTestLoadInstances(String.format("%s%s", DATA_URL_PREFIX, instancesEntity.getUuid()));
    }

    @Test
    void testLoadInstancesFromUrl() throws Exception {
        internalTestLoadInstances(HTTP_TEST_DATA_URL);
    }

    @Test
    void testDeleteInstancesSuccess() throws IOException {
        MockMultipartFile multipartFile = createInstancesMockMultipartFile();
        InstancesEntity instancesEntity = instancesService.uploadInstances(multipartFile);
        instancesService.deleteInstances(instancesEntity);
        assertThat(instancesRepository.existsById(instancesEntity.getId())).isFalse();
    }

    @Test
    void testDeleteInstancesWithError() throws IOException {
        MockMultipartFile multipartFile = createInstancesMockMultipartFile();
        InstancesEntity instancesEntity = instancesService.uploadInstances(multipartFile);
        doThrow(ProcessFileException.class).when(objectStorageService).removeObject(instancesEntity.getDataPath());
        assertThat(instancesRepository.existsById(instancesEntity.getId())).isTrue();
    }

    private void internalTestLoadInstances(String url) throws Exception {
        Instances expected = loadInstances();
        when(fileDataLoader.loadInstances()).thenReturn(expected);
        when(objectStorageService.getObject(anyString(), any())).thenReturn(expected);
        Instances actual = instancesService.loadInstances(url);
        assertThat(actual).isNotNull();
        assertThat(actual.relationName()).isEqualTo(expected.relationName());
        assertThat(actual.numInstances()).isEqualTo(expected.numInstances());
        assertThat(actual.numAttributes()).isEqualTo(expected.numAttributes());
        assertThat(actual.numClasses()).isEqualTo(expected.numClasses());
    }
}
