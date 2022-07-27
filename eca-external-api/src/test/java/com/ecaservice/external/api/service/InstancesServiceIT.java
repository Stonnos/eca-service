package com.ecaservice.external.api.service;

import com.ecaservice.external.api.repository.InstancesRepository;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import eca.data.file.FileDataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import weka.core.Instances;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link InstancesService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
class InstancesServiceIT {

    private static final String TEST_URL = "http://kt.ijs.si/Branax/Repository/WEKA/Iris.xls";

    private static final String EXPECTED_RELATION_NAME = "IrisEX";
    private static final int EXPECTED_NUM_INSTANCES = 150;
    private static final int EXPECTED_NUM_ATTRIBUTES = 5;

    @Mock
    private ObjectStorageService objectStorageService;
    @Mock
    private InstancesRepository instancesRepository;

    private InstancesService instancesService;

    @BeforeEach
    void init() {
        instancesService = new InstancesService(new FileDataLoader(), objectStorageService, instancesRepository);
    }

    @Test
    void testLoadDataFromExternalUrl() {
        Instances instances = instancesService.loadInstances(TEST_URL);
        assertThat(instances).isNotNull();
        assertThat(instances.relationName()).isEqualTo(EXPECTED_RELATION_NAME);
        assertThat(instances.numInstances()).isEqualTo(EXPECTED_NUM_INSTANCES);
        assertThat(instances.numAttributes()).isEqualTo(EXPECTED_NUM_ATTRIBUTES);
    }
}
