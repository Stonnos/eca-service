package com.ecaservice.data.storage.mapping;

import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.web.dto.model.InstancesDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.data.storage.TestHelperUtils.createInstancesEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link InstancesMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(InstancesMapperImpl.class)
class InstancesMapperTest {

    private static final long ID = 1L;

    @Inject
    private InstancesMapper instancesMapper;

    @Test
    void testMapInstancesEntity() {
        InstancesEntity instancesEntity = createInstancesEntity();
        instancesEntity.setId(ID);
        InstancesDto instancesDto = instancesMapper.map(instancesEntity);
        assertThat(instancesDto).isNotNull();
        assertThat(instancesDto.getTableName()).isEqualTo(instancesEntity.getTableName());
        assertThat(instancesDto.getNumInstances()).isEqualTo(instancesEntity.getNumInstances());
        assertThat(instancesDto.getNumAttributes()).isEqualTo(instancesEntity.getNumAttributes());
        assertThat(instancesDto.getCreated()).isEqualTo(instancesEntity.getCreated());
        assertThat(instancesDto.getCreatedBy()).isEqualTo(instancesEntity.getCreatedBy());
        assertThat(instancesDto.getId()).isEqualTo(instancesEntity.getId());
    }

    @Test
    void testMapInstancesEntities() {
        InstancesEntity instancesEntity = createInstancesEntity();
        List<InstancesDto> instancesDtoList = instancesMapper.map(Collections.singletonList(instancesEntity));
        assertThat(instancesDtoList).isNotNull().hasSize(1);
    }
}
