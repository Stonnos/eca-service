package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.AbstractJpaTest;
import com.ecaservice.data.storage.config.StorageTestConfiguration;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.repository.InstancesRepository;
import eca.data.db.SqlQueryHelper;
import eca.data.file.resource.FileResource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link StorageService} functionality.
 *
 * @author Roman Batygin
 */
@Import({StorageService.class, InstancesService.class, TransactionalService.class,
        SqlQueryHelper.class, StorageTestConfiguration.class})
class StorageServiceTest extends AbstractJpaTest {

    private static final String DATA_PATH = "german_credit.xls";
    private static final String TEST_TABLE = "test_table";

    @Inject
    private StorageService storageService;

    @Inject
    private InstancesRepository instancesRepository;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Override
    public void deleteAll() {
        instancesRepository.deleteAll();
    }

    @Test
    void testSaveData() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        FileResource fileResource = new FileResource(new File(classLoader.getResource(DATA_PATH).getFile()));
        InstancesEntity expected = storageService.saveData(fileResource, TEST_TABLE);
        InstancesEntity actual = instancesRepository.findById(expected.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getTableName()).isEqualTo(TEST_TABLE);
    }
}
