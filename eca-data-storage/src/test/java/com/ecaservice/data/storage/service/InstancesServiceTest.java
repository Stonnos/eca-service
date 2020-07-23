package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.AbstractJpaTest;
import com.ecaservice.data.storage.config.EcaDsConfig;
import eca.data.db.SqlQueryHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import weka.core.Instances;

import javax.inject.Inject;

import static com.ecaservice.data.storage.TestHelperUtils.loadInstances;

/**
 * Unit tests for checking {@link InstancesService} functionality.
 *
 * @author Roman Batygin
 */
@Import({InstancesService.class, TransactionalService.class, SqlQueryHelper.class, EcaDsConfig.class})
class InstancesServiceTest extends AbstractJpaTest {

    private static final String TABLE_NAME = "test_table";
    private static final String SELECT_COUNT_FORMAT = "SELECT count(*) FROM %s";

    @Inject
    private InstancesService instancesService;
    @Inject
    private JdbcTemplate jdbcTemplate;

    private Instances instances;

    @Override
    public void init() throws Exception {
        instances = loadInstances();
    }

    @Test
    void testSaveInstances() {
        instancesService.saveInstances(TABLE_NAME, instances);
        Integer result = jdbcTemplate.queryForObject(String.format(SELECT_COUNT_FORMAT, TABLE_NAME), Integer.class);
        Assertions.assertThat(result).isNotNull().isEqualTo(instances.numInstances());
    }
}
