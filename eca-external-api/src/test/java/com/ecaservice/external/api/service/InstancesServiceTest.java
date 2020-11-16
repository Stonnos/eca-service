package com.ecaservice.external.api.service;

import com.ecaservice.external.api.AbstractJpaTest;
import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.InstancesEntity;
import com.ecaservice.external.api.repository.InstancesRepository;
import eca.data.file.FileDataLoader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;

import javax.inject.Inject;

import java.io.IOException;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestDto;
import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestEntity;
import static com.ecaservice.external.api.TestHelperUtils.createInstancesMockMultipartFile;
import static com.ecaservice.external.api.TestHelperUtils.loadInstances;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link InstancesService} functionality.
 *
 * @author Roman Batygin
 */
@Import({ExternalApiConfig.class, InstancesService.class, FileDataLoader.class})
class InstancesServiceTest extends AbstractJpaTest {

    private static final String FILE_PATH_FORMAT = "%siris_%s.xls";

    @MockBean
    private FileDataService fileDataService;

    @Inject
    private InstancesRepository instancesRepository;

    @Inject
    private ExternalApiConfig externalApiConfig;

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
        assertThat(actual.getAbsolutePath()).isNotNull();
        String expectedDataPath =
                String.format(FILE_PATH_FORMAT, externalApiConfig.getTrainDataPath(), instancesEntity.getUuid());
        assertThat(actual.getAbsolutePath()).isEqualTo(expectedDataPath);
    }
}
