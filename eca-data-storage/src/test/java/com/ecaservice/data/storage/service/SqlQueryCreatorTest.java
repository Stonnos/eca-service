package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.model.ColumnModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.MessageFormat;
import java.util.List;

import static com.ecaservice.data.storage.TestHelperUtils.createPageRequestDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SearchQueryCreator} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class SqlQueryCreatorTest {

    private static final List<ColumnModel> COLUMNS = List.of(
            ColumnModel.builder()
                    .columnName("column1")
                    .dataType("character varying(255)")
                    .build(),
            ColumnModel.builder()
                    .columnName("column2")
                    .dataType("character varying(255)")
                    .build(),
            ColumnModel.builder()
                    .columnName("column3")
                    .dataType("character varying(255)")
                    .build(),
            ColumnModel.builder()
                    .columnName("column4")
                    .dataType("character varying(255)")
                    .build()
    );

    private static final String TABLE_NAME = "table";

    private static final String SEARCH_TERM = "term";
    private static final String SORT_FIELD = "column1";
    private static final String LIKE_FORMAT = "%{0}%";
    public static final String EXPECTED_SQL_QUERY = "select * from table where column1 like ? or column2 like ? or " +
            "column3 like ? or column4 like ? order by column1 desc limit 0 offset 0";

    @Mock
    private TableMetaDataProvider tableMetaDataProvider;

    @InjectMocks
    private SearchQueryCreator searchQueryCreator;

    @BeforeEach
    void init() {
        when(tableMetaDataProvider.getTableColumns(TABLE_NAME)).thenReturn(COLUMNS);
    }

    @Test
    void testBuildSqlQuery() {
        var pageRequestDto = createPageRequestDto();
        pageRequestDto.setSearchQuery(SEARCH_TERM);
        pageRequestDto.setSortField(SORT_FIELD);
        pageRequestDto.setAscending(false);
        var preparedSql = searchQueryCreator.buildSqlQuery(TABLE_NAME, pageRequestDto);
        assertThat(preparedSql).isNotNull();
        assertThat(preparedSql.getQuery()).isEqualTo(EXPECTED_SQL_QUERY);
        assertThat(preparedSql.getArgs()).hasSize(COLUMNS.size());
        for (var arg : preparedSql.getArgs()) {
            assertThat(arg).isEqualTo(MessageFormat.format(LIKE_FORMAT, SEARCH_TERM));
        }
    }
}
