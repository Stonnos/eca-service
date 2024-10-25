package com.ecaservice.server.mapping;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.web.dto.model.InstancesInfoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks {@link InstancesInfoMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(InstancesInfoMapperImpl.class)
class InstancesInfoMapperTest {

    @Autowired
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
        assertThat(instancesInfoDto.getId()).isEqualTo(instancesInfo.getId());
        assertThat(instancesInfoDto.getCreatedDate()).isEqualTo(instancesInfo.getCreatedDate());
    }

    @Test
    void testMapInstancesMetaInfoInfo() {
        var instancesMetaInfoInfo = TestHelperUtils.createInstancesMetaInfoInfo();
        var instancesMetaDataModel = instancesInfoMapper.map(instancesMetaInfoInfo);
        assertThat(instancesMetaDataModel.getUuid()).isEqualTo(instancesMetaInfoInfo.getUuid());
        assertThat(instancesMetaDataModel.getRelationName()).isEqualTo(instancesMetaInfoInfo.getRelationName());
        assertThat(instancesMetaDataModel.getClassName()).isEqualTo(instancesMetaInfoInfo.getClassName());
        assertThat(instancesMetaDataModel.getNumInstances()).isEqualTo(instancesMetaInfoInfo.getNumInstances());
        assertThat(instancesMetaDataModel.getNumAttributes()).isEqualTo(instancesMetaInfoInfo.getNumAttributes());
        assertThat(instancesMetaDataModel.getNumClasses()).isEqualTo(instancesMetaInfoInfo.getNumClasses());
        assertThat(instancesMetaDataModel.getMd5Hash()).isEqualTo(instancesMetaInfoInfo.getMd5Hash());
        assertThat(instancesMetaDataModel.getObjectPath()).isEqualTo(instancesMetaInfoInfo.getObjectPath());
        assertThat(instancesMetaDataModel.getAttributes()).hasSameSizeAs(instancesMetaInfoInfo.getAttributes());
    }
}
