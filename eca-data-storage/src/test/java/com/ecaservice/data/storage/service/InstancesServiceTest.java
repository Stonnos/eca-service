package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.AbstractJpaTest;
import com.ecaservice.data.storage.config.EcaDsConfig;
import eca.data.db.InstancesExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import weka.core.Instances;

import static com.ecaservice.data.storage.TestHelperUtils.createInstancesEntity;
import static com.ecaservice.data.storage.TestHelperUtils.loadInstances;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link InstancesService} functionality.
 *
 * @author Roman Batygin
 */
@Import({InstancesService.class, InstancesBatchService.class, EcaDsConfig.class,
        SearchQueryCreator.class, InstancesExtractor.class, AttributeService.class})
class InstancesServiceTest extends AbstractJpaTest {

    private static final String TABLE_NAME = "test_table";
    private static final String SELECT_COUNT_FORMAT = "SELECT count(*) FROM %s";

    @Autowired
    private InstancesService instancesService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Instances instances;

    @Override
    public void init() {
        instances = loadInstances();
    }

    @Test
    void testSaveInstances() {
        var instancesEntity = createInstancesEntity();
        instancesEntity.setTableName(TABLE_NAME);
        instancesService.saveInstances(instancesEntity, instances);
        Integer result = jdbcTemplate.queryForObject(String.format(SELECT_COUNT_FORMAT, TABLE_NAME), Integer.class);
        assertThat(result).isNotNull().isEqualTo(instances.numInstances());
    }
}
