package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.config.EcaDsConfig;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.repository.InstancesRepository;
import eca.data.db.SqlQueryHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import weka.core.Instances;

import javax.inject.Inject;

import static com.ecaservice.data.storage.TestHelperUtils.loadInstances;

/**
 * Unit tests for checking {@link InstancesService} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@AutoConfigureDataJpa
@EnableJpaRepositories(basePackageClasses = InstancesRepository.class)
@EntityScan(basePackageClasses = InstancesEntity.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({InstancesService.class, TransactionalService.class, SqlQueryHelper.class, EcaDsConfig.class})
class InstancesServiceTest {

    private static final String TABLE_NAME = "test_table";
    private static final String SELECT_COUNT_FORMAT = "SELECT count(*) FROM %s";

    @Inject
    private InstancesService instancesService;
    @Inject
    private JdbcTemplate jdbcTemplate;

    private Instances instances;

    @BeforeEach
    void init() throws Exception {
        instances = loadInstances();
    }

    @Test
    void testSaveInstances() {
        instancesService.saveInstances(TABLE_NAME, instances);
        Integer result = jdbcTemplate.queryForObject(String.format(SELECT_COUNT_FORMAT, TABLE_NAME), Integer.class);
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(instances.numInstances());
    }
}
