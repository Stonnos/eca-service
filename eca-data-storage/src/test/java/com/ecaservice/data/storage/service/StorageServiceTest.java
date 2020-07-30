package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.AbstractJpaTest;
import com.ecaservice.data.storage.config.StorageTestConfiguration;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.exception.DataStorageException;
import com.ecaservice.data.storage.exception.EntityNotFoundException;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.user.model.UserDetailsImpl;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.data.db.SqlQueryHelper;
import eca.data.file.resource.FileResource;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;
import java.io.File;
import java.util.Collections;

import static com.ecaservice.data.storage.TestHelperUtils.createInstancesEntity;
import static com.ecaservice.data.storage.entity.InstancesEntity_.CREATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

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
    private static final long ID = 2L;
    private static final String USER_NAME = "admin";

    @Inject
    private StorageService storageService;

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
    void testSaveData() {
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setUserName(USER_NAME);
        when(userService.getCurrentUser()).thenReturn(userDetails);
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        FileResource fileResource = new FileResource(new File(classLoader.getResource(DATA_PATH).getFile()));
        InstancesEntity expected = storageService.saveData(fileResource, TEST_TABLE);
        InstancesEntity actual = instancesRepository.findById(expected.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getTableName()).isEqualTo(TEST_TABLE);
        assertThat(actual.getCreatedBy()).isEqualTo(USER_NAME);
    }

    @Test
    void testSaveDataWithError() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        FileResource fileResource = new FileResource(new File(classLoader.getResource(DATA_PATH).getFile()));
        doThrow(DataIntegrityViolationException.class).when(jdbcTemplate).execute(anyString());
        assertThrows(DataStorageException.class, () -> {
            storageService.saveData(fileResource, TEST_TABLE);
        });
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
        assertThrows(EntityNotFoundException.class, () -> {
            storageService.deleteData(ID);
        });
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
