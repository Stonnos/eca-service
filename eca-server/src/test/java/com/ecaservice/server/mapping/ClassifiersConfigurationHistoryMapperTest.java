package com.ecaservice.server.mapping;

import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.model.entity.ClassifiersConfigurationActionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.time.LocalDateTime;

import static com.ecaservice.server.TestHelperUtils.createClassifiersConfigurationHistory;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for checking {@link ClassifiersConfigurationHistoryMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(ClassifiersConfigurationHistoryMapperImpl.class)
class ClassifiersConfigurationHistoryMapperTest {

    @Inject
    private ClassifiersConfigurationHistoryMapper classifiersConfigurationHistoryMapper;

    @Test
    void testMapClassifiersConfigurationHistoryEntity() {
        var classifiersConfigurationHistoryEntity =
                createClassifiersConfigurationHistory(new ClassifiersConfiguration(),
                        ClassifiersConfigurationActionType.CREATE_CONFIGURATION, LocalDateTime.now());
        var classifiersConfigurationHistoryDto =
                classifiersConfigurationHistoryMapper.map(classifiersConfigurationHistoryEntity);
        assertThat(classifiersConfigurationHistoryDto).isNotNull();
        assertThat(classifiersConfigurationHistoryDto.getCreatedAt()).isEqualTo(
                classifiersConfigurationHistoryEntity.getCreatedAt());
        assertThat(classifiersConfigurationHistoryDto.getCreatedBy()).isEqualTo(
                classifiersConfigurationHistoryEntity.getCreatedBy());
        assertThat(classifiersConfigurationHistoryDto.getMessageText()).isEqualTo(
                classifiersConfigurationHistoryEntity.getMessageText());
        assertThat(classifiersConfigurationHistoryDto.getActionType()).isNotNull();
        assertThat(classifiersConfigurationHistoryDto.getActionType().getValue()).isEqualTo(
                classifiersConfigurationHistoryEntity.getActionType().name());
        assertThat(classifiersConfigurationHistoryDto.getActionType().getDescription()).isEqualTo(
                classifiersConfigurationHistoryEntity.getActionType().getDescription());
    }
}
