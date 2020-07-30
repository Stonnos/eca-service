package com.ecaservice.data.storage.validation;

import com.ecaservice.data.storage.repository.InstancesRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ecaservice.data.storage.config.Constants.INSTANCES_TABLE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link UniqueTableNameValidator} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class UniqueTableNameValidatorTest {

    private static final String TABLE_NAME = "table";

    @Mock
    private InstancesRepository instancesRepository;

    private UniqueTableNameValidator uniqueTableNameValidator;

    @BeforeEach
    void init() {
        uniqueTableNameValidator = new UniqueTableNameValidator(instancesRepository);
    }

    @Test
    void testEmptyTableName() {
        assertThat(uniqueTableNameValidator.isValid(StringUtils.EMPTY, null)).isFalse();
    }

    @Test
    void testTableNameExists() {
        when(instancesRepository.existsByTableName(TABLE_NAME)).thenReturn(true);
        assertThat(uniqueTableNameValidator.isValid(TABLE_NAME, null)).isFalse();
    }

    @Test
    void testTableNameSameAsMetaInfoTableName() {
        assertThat(uniqueTableNameValidator.isValid(INSTANCES_TABLE_NAME, null)).isFalse();
    }

    @Test
    void testTableNameNotExists() {
        when(instancesRepository.existsByTableName(TABLE_NAME)).thenReturn(false);
        assertThat(uniqueTableNameValidator.isValid(TABLE_NAME, null)).isTrue();
    }
}
