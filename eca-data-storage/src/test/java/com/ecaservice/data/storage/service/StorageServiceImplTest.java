package com.ecaservice.data.storage.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.data.storage.AbstractJpaTest;
import com.ecaservice.data.storage.config.StorageTestConfiguration;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.user.model.UserDetailsImpl;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.data.db.SqlQueryHelper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import weka.core.Instances;

import javax.inject.Inject;
import java.util.Collections;

import static com.ecaservice.data.storage.TestHelperUtils.createInstancesEntity;
import static com.ecaservice.data.storage.TestHelperUtils.loadInstances;
import static com.ecaservice.data.storage.entity.InstancesEntity_.CREATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link StorageServiceImpl} functionality.
 *
 * @author Roman Batygin
 */
@Import({StorageServiceImpl.class, InstancesService.class, TransactionalService.class,
        SqlQueryHelper.class, StorageTestConfiguration.class})
class StorageServiceImplTest extends AbstractJpaTest {

    private static final String TEST_TABLE = "test_table";
    private static final String NEW_TABLE_NAME = "new_table_name";
    private static final long ID = 2L;
    private static final String USER_NAME = "admin";

    @Inject
    private StorageServiceImpl storageService;

    @Inject
    private InstancesRepository instancesRepository;

    @MockBean
    private JdbcTemplate jdbcTemplate;
    @MockBean
    private UserService userService;

    @Override
    public void deleteAll() {
        instancesRepository.deleteAll();
    }

    @Test
    void testSaveData() throws Exception {
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setUserName(USER_NAME);
        when(userService.getCurrentUser()).thenReturn(userDetails);
        Instances instances = loadInstances();
        InstancesEntity expected = storageService.saveData(instances, TEST_TABLE);
        InstancesEntity actual = instancesRepository.findById(expected.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getTableName()).isEqualTo(TEST_TABLE);
        assertThat(actual.getCreatedBy()).isEqualTo(USER_NAME);
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

    @Test
    void testDeleteNotExistingData() {
        createAndSaveInstancesEntity();
        assertThrows(EntityNotFoundException.class, () -> storageService.deleteData(ID));
    }

    @Test
    void testGetInstancesPage() {
        createAndSaveInstancesEntity();
        PageRequestDto pageRequestDto =
                new PageRequestDto(0, 10, CREATED, true, StringUtils.EMPTY, Collections.emptyList());
        Page<InstancesEntity> instancesEntityPage = storageService.getNextPage(pageRequestDto);
        assertThat(instancesEntityPage).isNotNull();
        assertThat(instancesEntityPage.getContent()).hasSize(1);
    }

    private InstancesEntity createAndSaveInstancesEntity() {
        InstancesEntity instancesEntity = createInstancesEntity();
        instancesEntity.setTableName(TEST_TABLE);
        return instancesRepository.save(instancesEntity);
    }
}
