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

import static com.ecaservice.data.storage.TestHelperUtils.createInstancesEntity;
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
    private static final String NEW_TABLE_NAME = "new_table_name";

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
        assertThat(actual.getTableName()).isEqualTo(TEST_TABLE);
    }

    @Test
    void testRenameData() {
        InstancesEntity instancesEntity = createAndSaveInstancesEntity();
        storageService.renameData(instancesEntity.getId(), NEW_TABLE_NAME);
        InstancesEntity actual = instancesRepository.findById(instancesEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getTableName()).isEqualTo(NEW_TABLE_NAME);
    }

    @Test
    void testDeleteData() {
        InstancesEntity instancesEntity = createAndSaveInstancesEntity();
        storageService.deleteData(instancesEntity.getId());
        assertThat(instancesRepository.existsById(instancesEntity.getId())).isFalse();
    }

    private InstancesEntity createAndSaveInstancesEntity() {
        InstancesEntity instancesEntity = createInstancesEntity();
        instancesEntity.setTableName(TEST_TABLE);
        return instancesRepository.save(instancesEntity);
    }
}
