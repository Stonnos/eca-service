package com.ecaservice.data.storage.mapping;

import com.ecaservice.data.storage.entity.AttributeEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.data.storage.TestHelperUtils.createNominalAttributeEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link AttributeMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(AttributeMapperImpl.class)
class AttributeMapperTest {

    private static final long ID = 1L;
    private static final String COLUMN_NAME = "column_name";

    @Inject
    private AttributeMapper attributeMapper;

    @Test
    void testMapAttributeEntity() {
        var attributeEntity = createNominalAttributeEntity(COLUMN_NAME, 0);
        attributeEntity.setId(ID);
        var attributeDto = attributeMapper.map(attributeEntity);
        assertThat(attributeDto).isNotNull();
        assertThat(attributeDto.getName()).isEqualTo(attributeEntity.getColumnName());
        assertThat(attributeDto.getIndex()).isEqualTo(attributeEntity.getIndex());
        assertThat(attributeDto.isSelected()).isEqualTo(attributeEntity.isSelected());
        assertThat(attributeDto.getType().getValue()).isEqualTo(attributeEntity.getType().name());
        assertThat(attributeDto.getType().getDescription()).isEqualTo(attributeEntity.getType().getDescription());
        assertThat(attributeDto.getValues()).hasSameSizeAs(attributeEntity.getValues());
        assertThat(attributeDto.getId()).isEqualTo(attributeEntity.getId());
    }
}
