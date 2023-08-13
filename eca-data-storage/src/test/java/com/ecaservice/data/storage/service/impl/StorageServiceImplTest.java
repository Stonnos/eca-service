package com.ecaservice.data.storage.service.impl;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.data.storage.AbstractJpaTest;
import com.ecaservice.data.storage.config.StorageTestConfiguration;
import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.AttributeType;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.entity.InstancesEntity_;
import com.ecaservice.data.storage.exception.ClassAttributeValuesIsTooLowException;
import com.ecaservice.data.storage.exception.InvalidClassAttributeTypeException;
import com.ecaservice.data.storage.mapping.AttributeMapperImpl;
import com.ecaservice.data.storage.repository.AttributeRepository;
import com.ecaservice.data.storage.repository.AttributeValueRepository;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.data.storage.service.AttributeService;
import com.ecaservice.data.storage.service.InstancesBatchService;
import com.ecaservice.data.storage.service.InstancesService;
import com.ecaservice.data.storage.service.InstancesTransformer;
import com.ecaservice.data.storage.service.SearchQueryCreator;
import com.ecaservice.data.storage.service.UserService;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.data.db.InstancesExtractor;
import eca.data.db.InstancesResultSetConverter;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import weka.core.Instances;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.ecaservice.data.storage.AssertionUtils.assertDataList;
import static com.ecaservice.data.storage.AssertionUtils.assertInstances;
import static com.ecaservice.data.storage.AssertionUtils.assertInstancesModel;
import static com.ecaservice.data.storage.TestHelperUtils.createAttributeEntity;
import static com.ecaservice.data.storage.TestHelperUtils.createAttributeValueEntity;
import static com.ecaservice.data.storage.TestHelperUtils.createInstancesEntity;
import static com.ecaservice.data.storage.TestHelperUtils.createPageRequestDto;
import static com.ecaservice.data.storage.TestHelperUtils.loadInstances;
import static com.ecaservice.data.storage.dictionary.FilterDictionaries.INSTANCES_TEMPLATE;
import static com.ecaservice.data.storage.entity.InstancesEntity_.CREATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link StorageServiceImpl} functionality.
 *
 * @author Roman Batygin
 */
@Import({StorageServiceImpl.class, InstancesService.class, InstancesBatchService.class,
        RandomValueStringGenerator.class, StorageTestConfiguration.class, AttributeService.class,
        AttributeMapperImpl.class, SearchQueryCreator.class, InstancesTransformer.class,
        InstancesResultSetConverter.class, InstancesExtractor.class})
class StorageServiceImplTest extends AbstractJpaTest {

    private static final String TEST_RELATION_NAME = "test_relation_name";
    private static final String NEW_RELATION_NAME = "new_relation_name";
    private static final long ID = 2L;
    private static final String USER_NAME = "admin";
    private static final String SEARCH_QUERY = "good";
    private static final int PAGE_SIZE = 100;

    private static final int EXPECTED_NUM_INSTANCES = 700;
    private static final String EXPECTED_CLASS_NAME = "class";
    private static final String DURATION_ATTRIBUTE = "duration";
    private static final String COLUMN_NAME = "column1";

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
    @MockBean
    private FilterService filterService;

    private Instances instances;

    private InstancesEntity instancesEntity;

    @Override
    public void init() {
        createAndSaveInstancesEntity();
        when(filterService.getGlobalFilterFields(INSTANCES_TEMPLATE)).thenReturn(
                List.of(
                        InstancesEntity_.ID,
                        InstancesEntity_.RELATION_NAME,
                        InstancesEntity_.CREATED_BY
                )
        );
    }

    @Override
    public void deleteAll() {
        unsetInstancesClass();
        attributeValueRepository.deleteAll();
        attributeRepository.deleteAll();
        instancesRepository.deleteAll();
    }

    @Test
    void testSaveData() {
        InstancesEntity expected = internalSaveData(TEST_RELATION_NAME);
        InstancesEntity actual = instancesRepository.findById(expected.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRelationName()).isEqualTo(TEST_RELATION_NAME);
        assertThat(actual.getCreatedBy()).isEqualTo(USER_NAME);
        assertThat(actual.getClassAttribute()).isNotNull();
        assertThat(actual.getClassAttribute().getAttributeName()).isEqualTo(EXPECTED_CLASS_NAME);
    }

    @Test
    void testRenameData() {
        storageService.renameData(instancesEntity.getId(), NEW_RELATION_NAME);
        InstancesEntity actual = instancesRepository.findById(instancesEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRelationName()).isEqualTo(NEW_RELATION_NAME);
    }

    @Test
    void testDeleteData() {
        var instances = internalSaveData(TEST_RELATION_NAME);
        storageService.deleteData(instances.getId());
        assertThat(instancesRepository.existsById(instances.getId())).isFalse();
        assertThat(attributeRepository.count()).isZero();
        assertThat(attributeValueRepository.count()).isZero();
    }

    @Test
    void testSetClassSuccess() {
        var instances = internalSaveData(TEST_RELATION_NAME);
        var classAttribute = getAttribute(instances, EXPECTED_CLASS_NAME);
        storageService.setClassAttribute(classAttribute.getId());
        InstancesEntity actual = instancesRepository.findById(instances.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getClassAttribute()).isNotNull();
        assertThat(actual.getClassAttribute().getId()).isEqualTo(classAttribute.getId());
    }

    @Test
    void testSetNumericClass() {
        var instances = internalSaveData(TEST_RELATION_NAME);
        var classAttribute = getAttribute(instances, DURATION_ATTRIBUTE);
        assertThrows(InvalidClassAttributeTypeException.class,
                () -> storageService.setClassAttribute(classAttribute.getId()));
    }

    @Test
    void testSetClassWithOneValue() {
        var classAttribute = createAttributeEntity("class", 0, AttributeType.NOMINAL);
        classAttribute.setInstancesEntity(instancesEntity);
        classAttribute.setValues(Collections.singletonList(createAttributeValueEntity("value", 0)));
        attributeRepository.save(classAttribute);
        assertThrows(ClassAttributeValuesIsTooLowException.class,
                () -> storageService.setClassAttribute(classAttribute.getId()));
    }

    @Test
    void testSelectAllAttributes() {
        var attribute = createAndSaveAttribute(false);
        storageService.selectAllAttributes(instancesEntity.getId());
        var actual = attributeRepository.findById(attribute.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.isSelected()).isTrue();
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
        var savedInstances = internalSaveData(TEST_RELATION_NAME);
        var pageRequest = createPageRequestDto();
        pageRequest.setSearchQuery(SEARCH_QUERY);
        pageRequest.setSize(PAGE_SIZE);
        var instancesPage = storageService.getData(savedInstances.getId(), pageRequest);
        assertThat(instancesPage).isNotNull();
        assertThat(instancesPage.getContent()).hasSize(PAGE_SIZE);
        assertThat(instancesPage.getTotalCount()).isEqualTo(EXPECTED_NUM_INSTANCES);
    }

    @Test
    void testGetTableFullData() {
        var savedInstances = internalSaveData(TEST_RELATION_NAME);
        var pageRequest = createPageRequestDto();
        pageRequest.setSize(instances.numInstances());
        var instancesPage = storageService.getData(savedInstances.getId(), pageRequest);
        assertThat(instancesPage).isNotNull();
        assertThat(instancesPage.getContent()).hasSize(instances.numInstances());
        assertThat(instancesPage.getTotalCount()).isEqualTo(instances.numInstances());
        assertDataList(instances, instancesPage.getContent());
    }

    @Test
    void testGetInstances() {
        var instancesEntity = internalSaveData(TEST_RELATION_NAME);
        var actual = storageService.getInstances(instancesEntity);
        assertInstances(instances, actual);
    }

    @Test
    void testGetValidInstancesModel() {
        var savedInstances = internalSaveData(TEST_RELATION_NAME);
        var instancesModel = storageService.getValidInstancesModel(savedInstances);
        assertInstancesModel(instances, instancesModel);
    }

    private InstancesEntity internalSaveData(String relationName) {
        when(userService.getCurrentUser()).thenReturn(USER_NAME);
        instances = loadInstances();
        return storageService.saveData(instances, relationName);
    }

    private void createAndSaveInstancesEntity() {
        instancesEntity = createInstancesEntity();
        instancesEntity.setTableName(TEST_RELATION_NAME);
        instancesEntity.setUuid(UUID.randomUUID().toString());
        instancesEntity.setIdColumnName(UUID.randomUUID().toString());
        instancesRepository.save(instancesEntity);
    }

    private void unsetInstancesClass() {
        var instancesList = instancesRepository.findAll();
        instancesList.forEach(instancesEntity -> instancesEntity.setClassAttribute(null));
        instancesRepository.saveAll(instancesList);
    }

    private AttributeEntity createAndSaveAttribute(boolean selected) {
        var attribute = createAttributeEntity(COLUMN_NAME, 0, AttributeType.NUMERIC);
        attribute.setSelected(selected);
        attribute.setInstancesEntity(instancesEntity);
        return attributeRepository.save(attribute);
    }

    private AttributeEntity getAttribute(InstancesEntity instances, String columnName) {
        return attributeRepository.findByInstancesEntityOrderByIndex(instances).stream()
                .filter(attributeEntity -> attributeEntity.getAttributeName().equals(columnName))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't find attribute [%s] for instances [%s]", columnName,
                                instances.getTableName())));
    }
}
