package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.AbstractJpaTest;
import com.ecaservice.data.storage.config.EcaDsConfig;
import com.ecaservice.data.storage.model.ColumnModel;
import eca.data.db.InstancesExtractor;
import eca.data.db.InstancesResultSetConverter;
import eca.data.db.SqlQueryHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import weka.core.Instances;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.data.storage.TestHelperUtils.createPageRequestDto;
import static com.ecaservice.data.storage.TestHelperUtils.loadInstances;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link InstancesService} functionality.
 *
 * @author Roman Batygin
 */
@Import({InstancesService.class, TransactionalService.class, SqlQueryHelper.class, EcaDsConfig.class,
        SearchQueryCreator.class, InstancesResultSetExtractor.class, InstancesResultSetConverter.class,
        InstancesExtractor.class, InstancesConversionService.class})
class InstancesServiceTest extends AbstractJpaTest {

    private static final String TABLE_NAME = "test_table";
    private static final String SELECT_COUNT_FORMAT = "SELECT count(*) FROM %s";

    private static final List<ColumnModel> COLUMNS = Collections.singletonList(
            ColumnModel.builder()
                    .columnName("class")
                    .dataType("character varying")
                    .build()
    );

    private static final String SEARCH_QUERY = "good";
    private static final String TABLE_2_NAME = "table2";
    private static final int PAGE_SIZE = 100;

    private static final int EXPECTED_NUM_INSTANCES = 700;

    @MockBean
    private TableMetaDataProvider tableMetaDataProvider;

    @Inject
    private InstancesService instancesService;
    @Inject
    private JdbcTemplate jdbcTemplate;

    private Instances instances;

    @Override
    public void init() {
        instances = loadInstances();
    }

    @Test
    void testSaveInstances() {
        instancesService.saveInstances(TABLE_NAME, instances);
        Integer result = jdbcTemplate.queryForObject(String.format(SELECT_COUNT_FORMAT, TABLE_NAME), Integer.class);
        assertThat(result).isNotNull().isEqualTo(instances.numInstances());
    }

    @Test
    void testGetInstances() {
        instancesService.saveInstances(TABLE_2_NAME, instances);
        when(tableMetaDataProvider.getTableColumns(TABLE_2_NAME)).thenReturn(COLUMNS);
        var pageRequest = createPageRequestDto();
        pageRequest.setSearchQuery(SEARCH_QUERY);
        pageRequest.setSize(PAGE_SIZE);
        var instancesPage = instancesService.getInstances(TABLE_2_NAME, pageRequest);
        assertThat(instancesPage).isNotNull();
        assertThat(instancesPage.getContent()).hasSize(PAGE_SIZE);
        assertThat(instancesPage.getTotalCount()).isEqualTo(EXPECTED_NUM_INSTANCES);
    }
}
