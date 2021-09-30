package com.ecaservice.server.mapping;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.web.dto.model.InstancesInfoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks {@link InstancesInfoMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(InstancesInfoMapperImpl.class)
class InstancesInfoMapperTest {

    @Inject
    private InstancesInfoMapper instancesInfoMapper;

    @Test
    void testMapInstancesInfo() {
        InstancesInfo instancesInfo = TestHelperUtils.createInstancesInfo();
        InstancesInfoDto instancesInfoDto = instancesInfoMapper.map(instancesInfo);
        assertThat(instancesInfoDto.getRelationName()).isEqualTo(instancesInfo.getRelationName());
        assertThat(instancesInfoDto.getClassName()).isEqualTo(instancesInfo.getClassName());
        assertThat(instancesInfoDto.getNumInstances()).isEqualTo(instancesInfo.getNumInstances());
        assertThat(instancesInfoDto.getNumAttributes()).isEqualTo(instancesInfo.getNumAttributes());
        assertThat(instancesInfoDto.getNumClasses()).isEqualTo(instancesInfo.getNumClasses());
    }
}
