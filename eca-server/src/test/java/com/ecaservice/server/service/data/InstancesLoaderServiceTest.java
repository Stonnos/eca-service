package com.ecaservice.server.service.data;

import com.ecaservice.s3.client.minio.databind.ObjectDeserializer;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.service.InstancesInfoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createInstancesInfo;
import static com.ecaservice.server.TestHelperUtils.loadInstancesModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link InstancesLoaderService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({InstancesLoaderService.class, InstancesInfoMapperImpl.class})
class InstancesLoaderServiceTest {

    @MockBean
    private InstancesInfoService instancesInfoService;
    @MockBean
    private ObjectStorageService objectStorageService;

    @Autowired
    private InstancesLoaderService instancesLoaderService;

    @Test
    void testLoadInstances() throws IOException, ClassNotFoundException {
        var instancesModel = loadInstancesModel();
        when(instancesInfoService.getOrSaveInstancesInfo(anyString())).thenReturn(createInstancesInfo());
        when(objectStorageService.getObject(anyString(), any(), any(ObjectDeserializer.class)))
                .thenReturn(instancesModel);
        var instances = instancesLoaderService.loadInstances(UUID.randomUUID().toString());
        assertThat(instances).isNotNull();
        assertThat(instances.numInstances()).isEqualTo(instancesModel.getInstances().size());
        assertThat(instances.numAttributes()).isEqualTo(instancesModel.getAttributes().size());
    }
}
