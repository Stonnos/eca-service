package com.ecaservice.server.service;

import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.data.InstancesMetaDataModel;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.model.entity.InstancesInfo_;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import weka.core.Instances;

import javax.inject.Inject;
import java.util.Collections;

import static com.ecaservice.server.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.server.PageRequestUtils.PAGE_SIZE;
import static com.ecaservice.server.TestHelperUtils.loadInstances;
import static com.ecaservice.server.model.entity.FilterTemplateType.INSTANCES_INFO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link  InstancesInfoService} class.
 *
 * @author Roman Batygin
 */
@Import({InstancesInfoService.class, InstancesInfoMapperImpl.class})
class InstancesInfoServiceTest extends AbstractJpaTest {

    private static final String DATA_MD_5_HASH = "3032e188204cb537f69fc7364f638641";

    @MockBean
    private FilterService filterService;

    @Inject
    private InstancesInfoRepository instancesInfoRepository;

    @Inject
    private InstancesInfoService instancesInfoService;

    private Instances data;

    @Override
    public void deleteAll() {
        instancesInfoRepository.deleteAll();
    }

    @Override
    public void init() {
        data = loadInstances();
        when(filterService.getGlobalFilterFields(INSTANCES_INFO)).thenReturn(
                Collections.singletonList(InstancesInfo_.RELATION_NAME));
    }

    @Test
    void testSaveNewInstancesInfo() {
        var instancesInfo = saveInstancesInfo(data);
        var actual = instancesInfoRepository.findById(instancesInfo.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRelationName()).isEqualTo(data.relationName());
        assertThat(actual.getNumInstances()).isEqualTo(data.numInstances());
        assertThat(actual.getNumAttributes()).isEqualTo(data.numAttributes());
        assertThat(actual.getClassName()).isEqualTo(data.classAttribute().name());
        assertThat(actual.getNumClasses()).isEqualTo(data.classAttribute().numValues());
        assertThat(actual.getUuid()).isNotNull();
        assertThat(actual.getCreatedDate()).isNotNull();
        assertThat(actual.getDataMd5Hash()).isNotNull();
    }

    @Test
    void testSaveExistingInstancesInfo() {
        saveInstancesInfo(data);
        saveInstancesInfo(data);
        assertThat(instancesInfoRepository.count()).isOne();
    }

    @Test
    void testGlobalSearch() {
        saveInstancesInfo(data);
        var pageRequestDto = new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, InstancesInfo_.CREATED_DATE, false,
                data.relationName(),
                Collections.emptyList());
        var instancesInfoPage = instancesInfoService.getNextPage(pageRequestDto);
        assertThat(instancesInfoPage).isNotNull();
        assertThat(instancesInfoPage.getPage()).isZero();
        assertThat(instancesInfoPage.getTotalCount()).isOne();
    }

    private InstancesInfo saveInstancesInfo(Instances data) {
        var instancesDataModel = new InstancesMetaDataModel(data.relationName(), data.numInstances(),
                data.numAttributes(), data.numClasses(), data.classAttribute().name(), DATA_MD_5_HASH, "instances");
        return instancesInfoService.getOrSaveInstancesInfo(instancesDataModel);
    }
}
