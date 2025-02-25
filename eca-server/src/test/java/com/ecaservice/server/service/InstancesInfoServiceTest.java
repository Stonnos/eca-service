package com.ecaservice.server.service;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.data.AttributeMetaInfo;
import com.ecaservice.server.model.data.AttributeType;
import com.ecaservice.server.model.data.InstancesMetaDataModel;
import com.ecaservice.server.model.entity.AttributesInfoEntity;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.model.entity.InstancesInfo_;
import com.ecaservice.server.repository.AttributesInfoRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.data.InstancesMetaDataService;
import com.ecaservice.web.dto.model.AttributeValueMetaInfoDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.SortFieldRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.ecaservice.server.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.server.PageRequestUtils.PAGE_SIZE;
import static com.ecaservice.server.model.entity.FilterTemplateType.INSTANCES_INFO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link  InstancesInfoService} class.
 *
 * @author Roman Batygin
 */
@Import({InstancesInfoService.class, InstancesInfoMapperImpl.class, InstancesProvider.class})
class InstancesInfoServiceTest extends AbstractJpaTest {

    private static final List<AttributeMetaInfo> ATTRIBUTE_META_INFO_LIST = Arrays.asList(
            new AttributeMetaInfo("a1", AttributeType.NUMERIC, null, null),
            new AttributeMetaInfo("class", AttributeType.NOMINAL, null, Arrays.asList("v1", "v2"))
    );
    private static final String CLASS_NAME = "class";
    private static final int NUM_CLASSES = 2;
    private static final String RELATION_NAME = "Relation";
    private static final int NUM_INSTANCES = 150;
    private static final int NUM_ATTRIBUTES = 2;
    private static final String OBJECT_PATH = "instances";

    @MockBean
    private FilterTemplateService filterTemplateService;
    @MockBean
    private InstancesMetaDataService instancesMetaDataService;

    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @Autowired
    private AttributesInfoRepository attributesInfoRepository;

    @Autowired
    private InstancesInfoService instancesInfoService;

    private final String dataUuid = UUID.randomUUID().toString();

    @Override
    public void deleteAll() {
        attributesInfoRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Override
    public void init() {
        when(filterTemplateService.getGlobalFilterFields(INSTANCES_INFO)).thenReturn(
                Collections.singletonList(InstancesInfo_.RELATION_NAME));
        var instancesDataModel =
                new InstancesMetaDataModel(dataUuid, RELATION_NAME, NUM_INSTANCES, NUM_ATTRIBUTES,
                        NUM_CLASSES, CLASS_NAME, OBJECT_PATH, ATTRIBUTE_META_INFO_LIST);
        when(instancesMetaDataService.getInstancesMetaData(dataUuid)).thenReturn(instancesDataModel);
    }

    @Test
    void testSaveNewInstancesInfo() {
        var instancesInfo = saveInstancesInfo();
        var actual = instancesInfoRepository.findById(instancesInfo.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRelationName()).isEqualTo(RELATION_NAME);
        assertThat(actual.getNumInstances()).isEqualTo(NUM_INSTANCES);
        assertThat(actual.getNumAttributes()).isEqualTo(NUM_ATTRIBUTES);
        assertThat(actual.getClassName()).isEqualTo(CLASS_NAME);
        assertThat(actual.getNumClasses()).isEqualTo(NUM_CLASSES);
        assertThat(actual.getObjectPath()).isNotNull();
        assertThat(actual.getUuid()).isNotNull();
        assertThat(actual.getCreatedDate()).isNotNull();
        AttributesInfoEntity attributesInfoEntity = attributesInfoRepository.findAll().getFirst();
        assertThat(attributesInfoEntity.getInstancesInfo().getId()).isEqualTo(actual.getId());
        assertThat(attributesInfoEntity.getAttributes()).isNotEmpty();
        assertAttributes(attributesInfoEntity);

    }

    @Test
    void testSaveExistingInstancesInfo() {
        saveInstancesInfo();
        saveInstancesInfo();
        assertThat(instancesInfoRepository.count()).isOne();
    }

    @Test
    void testGetClassValues() {
        InstancesInfo instancesInfo = saveInstancesInfo();
        List<AttributeValueMetaInfoDto> classValues = instancesInfoService.getClassValues(instancesInfo.getId());
        assertThat(classValues).isNotNull();
        assertThat(classValues).hasSize(NUM_CLASSES);
    }

    @Test
    void testGlobalSearch() {
        saveInstancesInfo();
        var pageRequestDto = new PageRequestDto(PAGE_NUMBER, PAGE_SIZE,
                Collections.singletonList(new SortFieldRequestDto(InstancesInfo_.CREATED_DATE, false)),
                RELATION_NAME, Collections.emptyList());
        var instancesInfoPage = instancesInfoService.getNextPage(pageRequestDto);
        assertThat(instancesInfoPage).isNotNull();
        assertThat(instancesInfoPage.getPage()).isZero();
        assertThat(instancesInfoPage.getTotalCount()).isOne();
    }

    private InstancesInfo saveInstancesInfo() {
        return instancesInfoService.getOrSaveInstancesInfo(dataUuid);
    }

    private void assertAttributes(AttributesInfoEntity attributesInfoEntity) {
        IntStream.range(0, attributesInfoEntity.getAttributes().size()).forEach(i -> {
            AttributeMetaInfo expectedAttribute = ATTRIBUTE_META_INFO_LIST.get(i);
            AttributeMetaInfo actualAttribute = attributesInfoEntity.getAttributes().get(i);
            assertThat(actualAttribute.getName()).isEqualTo(expectedAttribute.getName());
            assertThat(actualAttribute.getType().name()).isEqualTo(expectedAttribute.getType().name());
            if (AttributeType.NOMINAL.equals(expectedAttribute.getType())) {
                assertThat(actualAttribute.getValues()).isEqualTo(expectedAttribute.getValues());
            }
        });
    }
}
