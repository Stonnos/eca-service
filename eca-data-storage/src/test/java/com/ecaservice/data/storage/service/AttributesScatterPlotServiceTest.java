package com.ecaservice.data.storage.service;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.data.storage.AbstractJpaTest;
import com.ecaservice.data.storage.config.StorageTestConfiguration;
import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.mapping.AttributeMapperImpl;
import com.ecaservice.data.storage.mapping.InstancesMapperImpl;
import com.ecaservice.data.storage.repository.AttributeRepository;
import com.ecaservice.data.storage.repository.AttributeValueRepository;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.data.storage.service.impl.StorageServiceImpl;
import eca.data.db.InstancesExtractor;
import eca.data.db.InstancesResultSetConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import weka.core.Instances;

import java.util.UUID;

import static com.ecaservice.data.storage.TestHelperUtils.CREDIT_DATA_PATH;
import static com.ecaservice.data.storage.TestHelperUtils.loadInstances;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link AttributesScatterPlotService} functionality.
 *
 * @author Roman Batygin
 */
@Import({StorageServiceImpl.class, InstancesService.class, InstancesBatchService.class,
        StorageTestConfiguration.class, AttributeService.class,
        AttributeMapperImpl.class, SearchQueryCreator.class, InstancesTransformer.class, InstancesMapperImpl.class,
        InstancesResultSetConverter.class, InstancesExtractor.class, AttributesScatterPlotService.class})
class AttributesScatterPlotServiceTest extends AbstractJpaTest {

    private static final String USER_NAME = "admin";
    private static final String CHECKING_STATUS = "checking_status";
    private static final String DURATION = "duration";

    @Autowired
    private InstancesRepository instancesRepository;
    @Autowired
    private AttributeRepository attributeRepository;
    @Autowired
    private AttributeValueRepository attributeValueRepository;

    @MockBean
    private UserService userService;
    @MockBean
    private FilterTemplateService filterTemplateService;

    @Autowired
    private StorageServiceImpl storageService;

    @Autowired
    private AttributesScatterPlotService attributesScatterPlotService;

    private Instances instances;

    private InstancesEntity instancesEntity;

    @Override
    public void init() {
        internalSaveData();
    }

    @Override
    public void deleteAll() {
        unsetInstancesClass();
        attributeValueRepository.deleteAll();
        attributeRepository.deleteAll();
        instancesRepository.deleteAll();
    }

    @Test
    void testGetScatterPlot() {
        var attributes = attributeRepository.findAll();
        AttributeEntity xAttribute = attributes.stream()
                .filter(attributeEntity -> attributeEntity.getAttributeName().equals(CHECKING_STATUS))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't find attribute with code [%s]", CHECKING_STATUS)));
        AttributeEntity yAttribute = attributes.stream()
                .filter(attributeEntity -> attributeEntity.getAttributeName().equals(DURATION))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't find attribute with code [%s]", CHECKING_STATUS)));
        var scatterPlot = attributesScatterPlotService.getScatterPlot(instancesEntity.getId(), xAttribute.getId(),
                yAttribute.getId());
        assertThat(scatterPlot).isNotNull();
        assertThat(scatterPlot.getXAxisAttribute()).isNotNull();
        assertThat(scatterPlot.getXAxisAttribute().getName()).isEqualTo(xAttribute.getAttributeName());
        assertThat(scatterPlot.getYAxisAttribute()).isNotNull();
        assertThat(scatterPlot.getYAxisAttribute().getName()).isEqualTo(yAttribute.getAttributeName());
        assertThat(scatterPlot.getDataSets()).hasSize(instances.numClasses());
        int numItems = scatterPlot.getDataSets()
                .stream()
                .mapToInt(dataSet -> dataSet.getItems().size())
                .sum();
        assertThat(numItems).isEqualTo(instances.numInstances());
    }

    private void internalSaveData() {
        when(userService.getCurrentUser()).thenReturn(USER_NAME);
        instances = loadInstances(CREDIT_DATA_PATH);
        instancesEntity = storageService.saveData(instances, UUID.randomUUID().toString());
    }

    private void unsetInstancesClass() {
        var instancesList = instancesRepository.findAll();
        instancesList.forEach(instancesEntity -> instancesEntity.setClassAttribute(null));
        instancesRepository.saveAll(instancesList);
    }
}
