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
import com.ecaservice.web.dto.model.ContingencyTableReportDto;
import com.ecaservice.web.dto.model.ContingencyTableRequestDto;
import eca.data.db.InstancesExtractor;
import eca.data.db.InstancesResultSetConverter;
import eca.statistics.contingency.ContingencyTable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import weka.core.Instances;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.ecaservice.data.storage.TestHelperUtils.CREDIT_DATA_PATH;
import static com.ecaservice.data.storage.TestHelperUtils.loadInstances;
import static com.ecaservice.data.storage.util.Utils.toDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ContingencyTableReportService} functionality.
 *
 * @author Roman Batygin
 */
@Import({StorageServiceImpl.class, InstancesService.class, InstancesBatchService.class,
        StorageTestConfiguration.class, AttributeService.class,
        AttributeMapperImpl.class, SearchQueryCreator.class, InstancesTransformer.class, InstancesMapperImpl.class,
        InstancesResultSetConverter.class, InstancesExtractor.class, ContingencyTableReportService.class})
class ContingencyTableReportServiceTest extends AbstractJpaTest {

    private static final String USER_NAME = "admin";
    private static final String CHECKING_STATUS = "checking_status";
    private static final String CREDIT_HISTORY = "credit_history";
    private static final double ALPHA_VAL = 0.05d;

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
    private ContingencyTableReportService contingencyTableReportService;

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
    void testCalculateContingencyTableReport() {
        var attributes = attributeRepository.findAll();
        AttributeEntity xAttribute = attributes.stream()
                .filter(attributeEntity -> attributeEntity.getAttributeName().equals(CHECKING_STATUS))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't find attribute with code [%s]", CHECKING_STATUS)));
        AttributeEntity yAttribute = attributes.stream()
                .filter(attributeEntity -> attributeEntity.getAttributeName().equals(CREDIT_HISTORY))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't find attribute with code [%s]", CREDIT_HISTORY)));
        var requestDto = new ContingencyTableRequestDto(instancesEntity.getId(), xAttribute.getId(),
                yAttribute.getId(), BigDecimal.valueOf(ALPHA_VAL), false);
        var reportDto = contingencyTableReportService.calculateReport(requestDto);
        verifyReport(reportDto);
    }

    private void verifyReport(ContingencyTableReportDto reportDto) {
        ContingencyTable expectedContingencyTable = new ContingencyTable(instances);
        var xAttr = instances.attribute(CHECKING_STATUS);
        var yAttr = instances.attribute(CREDIT_HISTORY);
        double[][] expectedTable = expectedContingencyTable.computeContingencyMatrix(xAttr.index(), yAttr.index());
        var expectedTestResult =
                expectedContingencyTable.calculateChiSquaredResult(xAttr.index(), yAttr.index(), expectedTable);
        assertThat(reportDto).isNotNull();
        assertThat(reportDto.getAlpha()).isEqualTo(BigDecimal.valueOf(expectedTestResult.getAlpha()));
        assertThat(reportDto.getDf()).isEqualTo(expectedTestResult.getDf());
        assertThat(reportDto.getChiSquaredValue()).isEqualTo(toDecimal(expectedTestResult.getChiSquaredValue()));
        assertThat(reportDto.getChiSquaredCriticalValue()).isEqualTo(
                toDecimal(expectedTestResult.getChiSquaredCriticalValue()));
        assertThat(reportDto.isSignificant()).isEqualTo(expectedTestResult.isSignificant());

        assertThat(reportDto.getTableData().size()).isEqualTo(expectedTable.length);
        IntStream.range(0, expectedTable.length).forEach(i -> {
            assertThat(reportDto.getTableData().get(i).size()).isEqualTo(expectedTable[i].length);
            IntStream.range(0, expectedTable[0].length).forEach(j -> {
                assertThat(reportDto.getTableData().get(i).get(j)).isEqualTo((int) expectedTable[i][j]);
            });
        });
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
