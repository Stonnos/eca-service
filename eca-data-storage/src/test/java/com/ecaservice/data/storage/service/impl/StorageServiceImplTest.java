package com.ecaservice.data.storage.service.impl;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.data.storage.AbstractJpaTest;
import com.ecaservice.data.storage.config.StorageTestConfiguration;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.model.ColumnModel;
import com.ecaservice.data.storage.repository.AttributeRepository;
import com.ecaservice.data.storage.repository.AttributeValueRepository;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.data.storage.service.AttributeService;
import com.ecaservice.data.storage.service.InstancesConversionService;
import com.ecaservice.data.storage.service.InstancesResultSetExtractor;
import com.ecaservice.data.storage.service.InstancesService;
import com.ecaservice.data.storage.service.SearchQueryCreator;
import com.ecaservice.data.storage.service.TableMetaDataProvider;
import com.ecaservice.data.storage.service.TableNameService;
import com.ecaservice.data.storage.service.TransactionalService;
import com.ecaservice.data.storage.service.UserService;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.data.db.SqlQueryHelper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import weka.core.Instances;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

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
        SqlQueryHelper.class, StorageTestConfiguration.class, InstancesConversionService.class,
        AttributeService.class})
class StorageServiceImplTest extends AbstractJpaTest {

    private static final String TEST_TABLE = "test_table";
    private static final String TEST_TABLE_2 = "test_table_2";
    private static final String NEW_TABLE_NAME = "new_table_name";
    private static final long ID = 2L;
    private static final String USER_NAME = "admin";

    private static final List<ColumnModel> COLUMNS = Collections.singletonList(
            ColumnModel.builder()
                    .columnName("class")
                    .dataType("character varying")
                    .build()
    );

    @Inject
    private StorageServiceImpl storageService;

    @Inject
    private InstancesRepository instancesRepository;
    @Inject
    private AttributeRepository attributeRepository;
    @Inject
    private AttributeValueRepository attributeValueRepository;

    @MockBean
    private TableNameService tableNameService;
    @MockBean
    private UserService userService;
    @MockBean
    private SearchQueryCreator searchQueryCreator;
    @MockBean
    private TableMetaDataProvider tableMetaDataProvider;
    @MockBean
    private InstancesResultSetExtractor instancesResultSetExtractor;

    private InstancesEntity instancesEntity;

    @Override
    public void init() {
        createAndSaveInstancesEntity();
    }

    @Override
    public void deleteAll() {
        attributeValueRepository.deleteAll();
        attributeRepository.deleteAll();
        instancesRepository.deleteAll();
    }

    @Test
    void testSaveData() {
        when(tableNameService.tableExists(TEST_TABLE_2)).thenReturn(false);
        when(userService.getCurrentUser()).thenReturn(USER_NAME);
        Instances instances = loadInstances();
        InstancesEntity expected = storageService.saveData(instances, TEST_TABLE_2);
        InstancesEntity actual = instancesRepository.findById(expected.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getTableName()).isEqualTo(TEST_TABLE_2);
        assertThat(actual.getCreatedBy()).isEqualTo(USER_NAME);
    }

    @Test
    void testRenameData() {
        when(tableNameService.tableExists(TEST_TABLE)).thenReturn(false);
        storageService.renameData(instancesEntity.getId(), NEW_TABLE_NAME);
        InstancesEntity actual = instancesRepository.findById(instancesEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getTableName()).isEqualTo(NEW_TABLE_NAME);
    }

    @Test
    void testDeleteData() {
        storageService.deleteData(instancesEntity.getId());
        assertThat(instancesRepository.existsById(instancesEntity.getId())).isFalse();
    }

    @Test
    void testDeleteNotExistingData() {
        assertThrows(EntityNotFoundException.class, () -> storageService.deleteData(ID));
    }

    @Test
    void testGetInstancesPage() {
        PageRequestDto pageRequestDto =
                new PageRequestDto(0, 10, CREATED, true, StringUtils.EMPTY, Collections.emptyList());
        Page<InstancesEntity> instancesEntityPage = storageService.getNextPage(pageRequestDto);
        assertThat(instancesEntityPage).isNotNull();
        assertThat(instancesEntityPage.getContent()).hasSize(1);
    }

    @Test
    void testGetNotExistingData() {
        assertThrows(EntityNotFoundException.class, () -> storageService.getData(ID, null));
    }

    @Test
    void testGetAttributes() {
        when(tableMetaDataProvider.getTableColumns(instancesEntity.getTableName()))
                .thenReturn(COLUMNS);
        var actual = storageService.getAttributes(instancesEntity.getId());
        assertThat(actual).hasSameSizeAs(COLUMNS);
    }

    private void createAndSaveInstancesEntity() {
        instancesEntity = createInstancesEntity();
        instancesEntity.setTableName(TEST_TABLE);
        instancesRepository.save(instancesEntity);
    }
}
