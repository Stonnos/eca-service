package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.entity.InstancesInfo;
import com.ecaservice.web.dto.InstancesInfoDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks EvaluationLogMapper functionality {@see InstancesInfoMapper}.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(InstancesInfoMapperImpl.class)
public class InstancesInfoMapperTest {

    @Inject
    private InstancesInfoMapper instancesInfoMapper;

    @Test
    public void testMapInstancesInfo() {
        InstancesInfo instancesInfo = TestHelperUtils.createInstancesInfo();
        InstancesInfoDto instancesInfoDto = instancesInfoMapper.map(instancesInfo);
        assertThat(instancesInfoDto.getRelationName()).isEqualTo(instancesInfo.getRelationName());
        assertThat(instancesInfoDto.getClassName()).isEqualTo(instancesInfo.getClassName());
        assertThat(instancesInfoDto.getNumInstances()).isEqualTo(instancesInfo.getNumInstances());
        assertThat(instancesInfoDto.getNumAttributes()).isEqualTo(instancesInfo.getNumAttributes());
        assertThat(instancesInfoDto.getNumClasses()).isEqualTo(instancesInfo.getNumClasses());
    }
}
