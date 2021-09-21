package com.ecaservice.mail.mapping;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.mail.TestHelperUtils.createTemplateEntity;
import static com.ecaservice.mail.TestHelperUtils.createTemplateParameterEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link TemplateMapper} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(TemplateMapperImpl.class)
class TemplateMapperTest {

    private static final String PARAM = "param";

    @Inject
    private TemplateMapper templateMapper;

    @Test
    void testMapTemplateEntity() {
        var templateEntity = createTemplateEntity();
        templateEntity.getParameters().add(createTemplateParameterEntity(PARAM));
        var templateDto = templateMapper.map(templateEntity);
        assertThat(templateDto).isNotNull();
        assertThat(templateDto.getId()).isEqualTo(templateEntity.getId());
        assertThat(templateDto.getCode()).isEqualTo(templateEntity.getCode());
        assertThat(templateDto.getCreated()).isEqualTo(templateEntity.getCreated());
        assertThat(templateDto.getDescription()).isEqualTo(templateEntity.getDescription());
        assertThat(templateDto.getSubject()).isEqualTo(templateEntity.getSubject());
        assertThat(templateDto.getBody()).isEqualTo(templateEntity.getBody());
        assertThat(templateDto.getParameters()).hasSize(1);
    }

    @Test
    void testMapTemplateParameter() {
        var templateParameterEntity = createTemplateParameterEntity(PARAM);
        var templateParameterDto = templateMapper.map(templateParameterEntity);
        assertThat(templateParameterDto).isNotNull();
        assertThat(templateParameterDto.getId()).isEqualTo(templateParameterEntity.getId());
        assertThat(templateParameterDto.getCreated()).isEqualTo(templateParameterEntity.getCreated());
        assertThat(templateParameterDto.getDescription()).isEqualTo(templateParameterEntity.getDescription());
        assertThat(templateParameterDto.getParameterName()).isEqualTo(templateParameterEntity.getParameterName());
    }
}
