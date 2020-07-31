package com.ecaservice.data.storage.validation;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
    private JdbcTemplate jdbcTemplate;

    private UniqueTableNameValidator uniqueTableNameValidator;

    @BeforeEach
    void init() {
        uniqueTableNameValidator = new UniqueTableNameValidator(jdbcTemplate);
    }

    @Test
    void testEmptyTableName() {
        assertThat(uniqueTableNameValidator.isValid(StringUtils.EMPTY, null)).isFalse();
    }

    @Test
    void testTableNameExists() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class))).thenReturn(false);
        assertThat(uniqueTableNameValidator.isValid(TABLE_NAME, null)).isFalse();
    }

    @Test
    void testTableNameNotExists() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class))).thenReturn(true);
        assertThat(uniqueTableNameValidator.isValid(TABLE_NAME, null)).isTrue();
    }
}
