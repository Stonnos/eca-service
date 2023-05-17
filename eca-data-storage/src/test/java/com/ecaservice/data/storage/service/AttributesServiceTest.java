package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.AbstractJpaTest;
import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.repository.AttributeRepository;
import com.ecaservice.data.storage.repository.AttributeValueRepository;
import com.ecaservice.data.storage.repository.InstancesRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import weka.core.Attribute;
import weka.core.Instances;

import javax.inject.Inject;

import java.util.stream.IntStream;

import static com.ecaservice.data.storage.TestHelperUtils.createInstancesEntity;
import static com.ecaservice.data.storage.TestHelperUtils.loadInstances;
import static com.ecaservice.data.storage.util.Utils.getAttributeType;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link AttributeService} functionality.
 *
 * @author Roman Batygin
 */
@Import(AttributeService.class)
class AttributesServiceTest extends AbstractJpaTest {

    private static final String TEST_TABLE = "test_table";

    @Inject
    private InstancesRepository instancesRepository;
    @Inject
    private AttributeRepository attributeRepository;
    @Inject
    private AttributeValueRepository attributeValueRepository;

    @Inject
    private AttributeService attributeService;

    private Instances instances;

    private InstancesEntity instancesEntity;

    @Override
    public void init() {
        instances = loadInstances();
        createAndSaveInstancesEntity();
    }

    @Override
    public void deleteAll() {
        attributeValueRepository.deleteAll();
        attributeRepository.deleteAll();
        instancesRepository.deleteAll();
    }

    @Test
    void testSaveAttributes() {
        attributeService.saveAttributes(instancesEntity, instances);
        verifySavedAttributes();
    }

    private void createAndSaveInstancesEntity() {
        instancesEntity = createInstancesEntity();
        instancesEntity.setTableName(TEST_TABLE);
        instancesRepository.save(instancesEntity);
    }

    private void verifySavedAttributes() {
        var attributes = attributeRepository.findByInstancesEntityOrderByIndex(instancesEntity);
        IntStream.range(0, instances.numAttributes()).forEach(i -> {
            var expectedAttribute = instances.attribute(i);
            var actualAttributeEntity = attributes.get(i);
            assertThat(actualAttributeEntity.getColumnName()).isEqualTo(expectedAttribute.name());
            assertThat(actualAttributeEntity.getIndex()).isEqualTo(expectedAttribute.index());
            var expectedAttributeType = getAttributeType(expectedAttribute);
            assertThat(actualAttributeEntity.getType()).isEqualTo(expectedAttributeType);
            assertThat(actualAttributeEntity.isSelected()).isTrue();
            if (expectedAttribute.isNominal()) {
                verifySavedAttributeValues(expectedAttribute, actualAttributeEntity);
            } else {
                assertThat(actualAttributeEntity.getValues()).isEmpty();
            }
        });
    }

    private void verifySavedAttributeValues(Attribute expectedAttribute, AttributeEntity actualAttributeEntity) {
        IntStream.range(0, expectedAttribute.numValues()).forEach(i -> {
            var attributeValueEntity = actualAttributeEntity.getValues().get(i);
            String expectedValue = expectedAttribute.value(i);
            String actualValue = attributeValueEntity.getValue();
            assertThat(actualValue).isEqualTo(expectedValue);
            assertThat(attributeValueEntity.getValueOrder()).isEqualTo(i);
        });
    }
}
