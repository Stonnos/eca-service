package com.ecaservice.data.storage.service;

import com.ecaservice.core.filter.exception.FieldNotFoundException;
import com.ecaservice.data.storage.entity.AttributeType;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.model.AttributeInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.ecaservice.data.storage.TestHelperUtils.createInstancesEntity;
import static com.ecaservice.data.storage.TestHelperUtils.createPageRequestDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SearchQueryCreator} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class SearchQueryCreatorTest {

    private static final List<AttributeInfo> ATTRIBUTES = List.of(
           new AttributeInfo("column1", AttributeType.NOMINAL),
            new AttributeInfo("column2", AttributeType.NOMINAL),
            new AttributeInfo("column3", AttributeType.NOMINAL),
            new AttributeInfo("column4", AttributeType.NUMERIC)
    );

    private static final String TABLE_NAME = "table";

    private static final String SEARCH_TERM = "2.35";
    private static final String SORT_FIELD = "column1";
    public static final String EXPECTED_SQL_QUERY = "select * from table where lower(column1) like ? " +
            "or lower(column2) like ? or lower(column3) like ? or column4 = ? order by column1 desc limit 10 offset 0";
    public static final String EXPECTED_SQL_COUNT_QUERY =
            "select count(*) from table where lower(column1) like ? or lower(column2) like ? or lower(column3) like ?" +
                    " or column4 = ?";
    private static final String INVALID_SORT_FIELD = "avc";

    @Mock
    private AttributeService attributeService;

    @InjectMocks
    private SearchQueryCreator searchQueryCreator;

    private InstancesEntity instancesEntity;

    @BeforeEach
    void init() {
        instancesEntity = createInstancesEntity();
        instancesEntity.setTableName(TABLE_NAME);
        when(attributeService.getsAttributesInfo(any(InstancesEntity.class))).thenReturn(ATTRIBUTES);
    }

    @Test
    void testBuildSqlQuery() {
        var pageRequestDto = createPageRequestDto();
        pageRequestDto.setSearchQuery(SEARCH_TERM);
        pageRequestDto.setSortField(SORT_FIELD);
        pageRequestDto.setAscending(false);
        var preparedSql = searchQueryCreator.buildSqlQuery(instancesEntity, pageRequestDto);
        assertThat(preparedSql).isNotNull();
        assertThat(preparedSql.getQuery()).isEqualTo(EXPECTED_SQL_QUERY);
        assertThat(preparedSql.getCountQuery()).isEqualTo(EXPECTED_SQL_COUNT_QUERY);
        assertThat(preparedSql.getArgs()).hasSize(ATTRIBUTES.size());
    }

    @Test
    void testBuildSqlQueryWithInvalidSortField() {
        var pageRequestDto = createPageRequestDto();
        pageRequestDto.setSortField(INVALID_SORT_FIELD);
        assertThrows(FieldNotFoundException.class, () -> searchQueryCreator.buildSqlQuery(instancesEntity, pageRequestDto));
    }

}
