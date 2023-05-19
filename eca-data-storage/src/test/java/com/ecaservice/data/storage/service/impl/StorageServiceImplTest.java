package com.ecaservice.data.storage.service.impl;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.data.storage.AbstractJpaTest;
import com.ecaservice.data.storage.config.StorageTestConfiguration;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.mapping.AttributeMapperImpl;
import com.ecaservice.data.storage.repository.AttributeRepository;
import com.ecaservice.data.storage.repository.AttributeValueRepository;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.data.storage.service.AttributeService;
import com.ecaservice.data.storage.service.InstancesConversionService;
import com.ecaservice.data.storage.service.InstancesResultSetExtractor;
import com.ecaservice.data.storage.service.InstancesService;
import com.ecaservice.data.storage.service.SearchQueryCreator;
import com.ecaservice.data.storage.service.TransactionalService;
import com.ecaservice.data.storage.service.UserService;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.data.db.InstancesExtractor;
import eca.data.db.InstancesResultSetConverter;
import eca.data.db.SqlQueryHelper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import weka.core.Instances;

import javax.inject.Inject;
import java.util.Collections;

import static com.ecaservice.data.storage.TestHelperUtils.createInstancesEntity;
import static com.ecaservice.data.storage.TestHelperUtils.createPageRequestDto;
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
        AttributeService.class, AttributeMapperImpl.class, SearchQueryCreator.class,
        InstancesResultSetExtractor.class, InstancesResultSetConverter.class,
        InstancesExtractor.class, InstancesConversionService.class})
class StorageServiceImplTest extends AbstractJpaTest {

    private static final String TEST_TABLE = "test_table";
    private static final String TEST_TABLE_2 = "test_table_2";
    private static final String TEST_TABLE_3 = "test_table_3";
    private static final String TEST_TABLE_4 = "test_table_4";
    private static final String NEW_TABLE_NAME = "new_table_name";
    private static final long ID = 2L;
    private static final String USER_NAME = "admin";
    private static final String SEARCH_QUERY = "good";
    private static final int PAGE_SIZE = 100;

    private static final int EXPECTED_NUM_INSTANCES = 700;

    @Inject
    private StorageServiceImpl storageService;

    @Inject
    private InstancesRepository instancesRepository;
    @Inject
    private AttributeRepository attributeRepository;
    @Inject
    private AttributeValueRepository attributeValueRepository;

    @MockBean
    private UserService userService;

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
        InstancesEntity expected = internalSaveData(TEST_TABLE_2);
        InstancesEntity actual = instancesRepository.findById(expected.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getTableName()).isEqualTo(TEST_TABLE_2);
        assertThat(actual.getCreatedBy()).isEqualTo(USER_NAME);
    }

    @Test
    void testRenameData() {
        storageService.renameData(instancesEntity.getId(), NEW_TABLE_NAME);
        InstancesEntity actual = instancesRepository.findById(instancesEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getTableName()).isEqualTo(NEW_TABLE_NAME);
    }

    @Test
    void testDeleteData() {
        var instances = internalSaveData(TEST_TABLE_4);
        storageService.deleteData(instances.getId());
        assertThat(instancesRepository.existsById(instances.getId())).isFalse();
        assertThat(attributeRepository.count()).isZero();
        assertThat(attributeValueRepository.count()).isZero();
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
    void testGetTableDataWithPageParams() {
        var instances = internalSaveData(TEST_TABLE_3);
        var pageRequest = createPageRequestDto();
        pageRequest.setSearchQuery(SEARCH_QUERY);
        pageRequest.setSize(PAGE_SIZE);
        var instancesPage = storageService.getData(instances.getId(), pageRequest);
        assertThat(instancesPage).isNotNull();
        assertThat(instancesPage.getContent()).hasSize(PAGE_SIZE);
        assertThat(instancesPage.getTotalCount()).isEqualTo(EXPECTED_NUM_INSTANCES);
    }

    private InstancesEntity internalSaveData(String tableName) {
        when(userService.getCurrentUser()).thenReturn(USER_NAME);
        Instances instances = loadInstances();
        return storageService.saveData(instances, tableName);
    }

    private void createAndSaveInstancesEntity() {
        instancesEntity = createInstancesEntity();
        instancesEntity.setTableName(TEST_TABLE);
        instancesRepository.save(instancesEntity);
    }
}
