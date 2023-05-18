package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.AbstractJpaTest;
import com.ecaservice.data.storage.TestHelperUtils;
import com.ecaservice.data.storage.config.EcaDsConfig;
import com.ecaservice.data.storage.entity.AttributeType;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.model.AttributeInfo;
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

    private static final String SEARCH_QUERY = "good";
    private static final String TABLE_2_NAME = "table2";
    private static final String TABLE_3_NAME = "table3";
    private static final int PAGE_SIZE = 100;

    private static final int EXPECTED_NUM_INSTANCES = 700;

    @MockBean
    private AttributeService attributeService;

    @Inject
    private InstancesService instancesService;
    @Inject
    private JdbcTemplate jdbcTemplate;

    private Instances instances;

    private InstancesEntity instancesEntity;

    @Override
    public void init() {
        instances = loadInstances();
        createInstancesEntity();
    }

    @Test
    void testSaveInstances() {
        instancesService.saveInstances(TABLE_NAME, instances);
        Integer result = jdbcTemplate.queryForObject(String.format(SELECT_COUNT_FORMAT, TABLE_NAME), Integer.class);
        assertThat(result).isNotNull().isEqualTo(instances.numInstances());
    }

    @Test
    void testGetInstancesWithPageParams() {
        instancesService.saveInstances(TABLE_2_NAME, instances);
        var attributes = Collections.singletonList(new AttributeInfo("class", AttributeType.NOMINAL));
        when(attributeService.getsAttributesInfo(instancesEntity)).thenReturn(attributes);
        var pageRequest = createPageRequestDto();
        pageRequest.setSearchQuery(SEARCH_QUERY);
        pageRequest.setSize(PAGE_SIZE);
        var instancesPage = instancesService.getInstances(instancesEntity, pageRequest);
        assertThat(instancesPage).isNotNull();
        assertThat(instancesPage.getContent()).hasSize(PAGE_SIZE);
        assertThat(instancesPage.getTotalCount()).isEqualTo(EXPECTED_NUM_INSTANCES);
    }

    @Test
    void testGetInstances() {
        instancesService.saveInstances(TABLE_3_NAME, instances);
        var actual = instancesService.getInstances(TABLE_3_NAME);
        assertThat(actual).hasSameSizeAs(instances);
        assertThat(actual.numAttributes()).isEqualTo(instances.numAttributes());
    }

    private void createInstancesEntity() {
        instancesEntity = TestHelperUtils.createInstancesEntity();
        instancesEntity.setTableName(TABLE_2_NAME);
    }
}
