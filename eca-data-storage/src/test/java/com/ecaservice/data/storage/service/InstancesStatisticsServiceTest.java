package com.ecaservice.data.storage.service;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.data.storage.AbstractJpaTest;
import com.ecaservice.data.storage.config.StorageTestConfiguration;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.mapping.AttributeMapperImpl;
import com.ecaservice.data.storage.mapping.InstancesMapperImpl;
import com.ecaservice.data.storage.repository.AttributeRepository;
import com.ecaservice.data.storage.repository.AttributeValueRepository;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.data.storage.service.impl.StorageServiceImpl;
import com.ecaservice.web.dto.model.AttributeStatisticsDto;
import com.ecaservice.web.dto.model.FrequencyDiagramDataDto;
import com.ecaservice.web.dto.model.InstancesStatisticsDto;
import eca.data.db.InstancesExtractor;
import eca.data.db.InstancesResultSetConverter;
import eca.statistics.AttributeStatistics;
import eca.statistics.diagram.FrequencyData;
import eca.statistics.diagram.FrequencyDiagramBuilder;
import eca.text.NumericFormatFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Instances;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.ecaservice.data.storage.TestHelperUtils.CREDIT_DATA_PATH;
import static com.ecaservice.data.storage.TestHelperUtils.GLASS_DATA_PATH;
import static com.ecaservice.data.storage.TestHelperUtils.IONOSPHERE_DATA_PATH;
import static com.ecaservice.data.storage.TestHelperUtils.loadInstances;
import static com.ecaservice.data.storage.TestHelperUtils.numAttributes;
import static com.ecaservice.data.storage.util.Utils.toDecimal;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link InstancesStatisticsService} functionality.
 *
 * @author Roman Batygin
 */
@Import({StorageServiceImpl.class, InstancesService.class, InstancesBatchService.class,
        StorageTestConfiguration.class, AttributeService.class,
        AttributeMapperImpl.class, SearchQueryCreator.class, InstancesTransformer.class, InstancesMapperImpl.class,
        InstancesResultSetConverter.class, InstancesExtractor.class, InstancesStatisticsService.class})
class InstancesStatisticsServiceTest extends AbstractJpaTest {

    private static final String USER_NAME = "admin";
    private static final int SCALE = 4;

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
    private InstancesStatisticsService instancesStatisticsService;

    private Instances instances;

    private InstancesEntity instancesEntity;

    @Override
    public void deleteAll() {
        unsetInstancesClass();
        attributeValueRepository.deleteAll();
        attributeRepository.deleteAll();
        instancesRepository.deleteAll();
    }

    @Test
    void testGetInstancesStatisticsForCreditDataSet() {
        internalSaveData(CREDIT_DATA_PATH);
        InstancesStatisticsDto instancesStatisticsDto =
                instancesStatisticsService.getInstancesStatistics(instancesEntity.getId());
        assertThat(instancesStatisticsDto).isNotNull();
        assertThat(instancesStatisticsDto.getId()).isEqualTo(instancesEntity.getId());
        assertThat(instancesStatisticsDto.getNumInstances()).isEqualTo(instances.numInstances());
        assertThat(instancesStatisticsDto.getNumAttributes()).isEqualTo(instances.numAttributes());
        assertThat(instancesStatisticsDto.getNumClasses()).isEqualTo(instances.numClasses());
        assertThat(instancesStatisticsDto.getClassName()).isEqualTo(instances.classAttribute().name());
        int expectedNumNumericAttributes = numAttributes(instances, Attribute.NUMERIC);
        int expectedNumNominalAttributes = numAttributes(instances, Attribute.NOMINAL);
        assertThat(instancesStatisticsDto.getNumDateAttributes()).isZero();
        assertThat(instancesStatisticsDto.getNumNumericAttributes()).isEqualTo(expectedNumNumericAttributes);
        assertThat(instancesStatisticsDto.getNumNominalAttributes()).isEqualTo(expectedNumNominalAttributes);
    }

    @Test
    void testCalculateAttributeStatisticsForCreditDataSet() {
        internalSaveData(CREDIT_DATA_PATH);
        verifyAttributeStatistics();
    }

    @Test
    void testCalculateAttributeStatisticsForIonosphereDataSet() {
        internalSaveData(IONOSPHERE_DATA_PATH);
        verifyAttributeStatistics();
    }

    @Test
    void testCalculateAttributeStatisticsForGlassDataSet() {
        internalSaveData(GLASS_DATA_PATH);
        verifyAttributeStatistics();
    }

    private void verifyAttributeStatistics() {
        var attributes = attributeRepository.findAll();
        attributes.forEach(attributeEntity -> {
            var attributeStatistics = instancesStatisticsService.getAttributeStatistics(attributeEntity.getId());
            Attribute expectedAttribute = instances.attribute(attributeEntity.getAttributeName());
            assertThat(attributeStatistics).isNotNull();
            assertThat(attributeStatistics.getName()).isEqualTo(expectedAttribute.name());

            AttributeStats expectedAttributeStats = instances.attributeStats(expectedAttribute.index());
            if (expectedAttribute.isNominal()) {
                int[] nominalCounts = expectedAttributeStats.nominalCounts;
                IntStream.range(0, attributeStatistics.getFrequencyDiagramValues().size()).forEach(i -> {
                    FrequencyDiagramDataDto frequencyDiagramDataDto =
                            attributeStatistics.getFrequencyDiagramValues().get(i);
                    assertThat(frequencyDiagramDataDto.getCode()).isEqualTo(expectedAttribute.value(i));
                    assertThat(frequencyDiagramDataDto.getFrequency()).isEqualTo(nominalCounts[i]);
                });
                verifyFrequencySumValues(attributeStatistics);
            } else {
                BigDecimal expectedMin = toDecimal(expectedAttributeStats.numericStats.min, SCALE);
                BigDecimal expectedMax = toDecimal(expectedAttributeStats.numericStats.max, SCALE);
                BigDecimal expectedMean = toDecimal(expectedAttributeStats.numericStats.mean, SCALE);
                BigDecimal expectedStdDev = toDecimal(expectedAttributeStats.numericStats.stdDev, SCALE);
                BigDecimal expectedVariance = toDecimal(
                        expectedAttributeStats.numericStats.stdDev * expectedAttributeStats.numericStats.stdDev, SCALE);
                assertThat(attributeStatistics.getMinValue()).isEqualTo(expectedMin);
                assertThat(attributeStatistics.getMaxValue()).isEqualTo(expectedMax);
                assertThat(attributeStatistics.getMeanValue()).isEqualTo(expectedMean);
                assertThat(attributeStatistics.getStdDevValue()).isEqualTo(expectedStdDev);
                assertThat(attributeStatistics.getVarianceValue()).isEqualTo(expectedVariance);
                verifyFrequencyDiagramDataForNumericAttribute(attributeStatistics, expectedAttribute);
            }
        });
    }

    /**
     * Verifies frequency diagram data with frequency diagram data from eca core library.
     *
     * @param attributeStatisticsDto - attribute statistics dto
     * @param expectedAttribute      - expected attribute
     */
    private void verifyFrequencyDiagramDataForNumericAttribute(AttributeStatisticsDto attributeStatisticsDto,
                                                               Attribute expectedAttribute) {
        AttributeStatistics attributeStatistics =
                new AttributeStatistics(instances, NumericFormatFactory.getInstance());
        FrequencyDiagramBuilder frequencyDiagramBuilder = new FrequencyDiagramBuilder(attributeStatistics);
        List<FrequencyData> expectedFrequencyDataList =
                frequencyDiagramBuilder.calculateFrequencyDiagramDataForNumericAttribute(expectedAttribute);
        IntStream.range(0, expectedFrequencyDataList.size()).forEach(i -> {
            FrequencyData expected = expectedFrequencyDataList.get(i);
            FrequencyDiagramDataDto actual = attributeStatisticsDto.getFrequencyDiagramValues().get(i);
            assertThat(actual.getLowerBound()).isEqualTo(toDecimal(expected.getLowerBound(), SCALE));
            assertThat(actual.getUpperBound()).isEqualTo(toDecimal(expected.getUpperBound(), SCALE));
            assertThat(actual.getFrequency()).isEqualTo(expected.getFrequency());
        });
        verifyFrequencySumValues(attributeStatisticsDto);
    }

    private void verifyFrequencySumValues(AttributeStatisticsDto attributeStatisticsDto) {
        int actualNumValues = attributeStatisticsDto.getFrequencyDiagramValues()
                .stream()
                .mapToInt(FrequencyDiagramDataDto::getFrequency)
                .sum();
        assertThat(actualNumValues).isEqualTo(instances.numInstances());
    }

    private void internalSaveData(String path) {
        when(userService.getCurrentUser()).thenReturn(USER_NAME);
        instances = loadInstances(path);
        instancesEntity = storageService.saveData(instances, UUID.randomUUID().toString());
    }

    private void unsetInstancesClass() {
        var instancesList = instancesRepository.findAll();
        instancesList.forEach(instancesEntity -> instancesEntity.setClassAttribute(null));
        instancesRepository.saveAll(instancesList);
    }
}
