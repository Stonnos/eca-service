package com.ecaservice.data.storage.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link TableNameServiceImpl} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class TableNameServiceImplTest {

    private static final String TABLE_NAME = "table_name";

    @Mock
    private JdbcTemplate jdbcTemplate;
    @InjectMocks
    private TableNameServiceImpl tableNameTestService;

    @Test
    void testTableExists() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class))).thenReturn(true);
        boolean result = tableNameTestService.tableExists(TABLE_NAME);
        assertThat(result).isTrue();
    }
}
