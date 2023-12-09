package com.ecaservice.ers.service;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.ers.AbstractJpaTest;
import com.ecaservice.ers.mapping.InstancesMapperImpl;
import com.ecaservice.ers.model.InstancesInfo_;
import com.ecaservice.ers.repository.InstancesInfoRepository;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;

import static com.ecaservice.ers.TestHelperUtils.buildInstancesInfo;
import static com.ecaservice.ers.dictionary.FilterDictionaries.INSTANCES_INFO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link InstancesDataService} class.
 *
 * @author Roman Batygin
 */
@Import({InstancesDataService.class, InstancesMapperImpl.class})
class InstancesInfoDataServiceTest extends AbstractJpaTest {

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final String RELATION_1 = "relation1";
    private static final String RELATION_2 = "relation2";

    @MockBean
    private FilterTemplateService filterTemplateService;

    @Inject
    private InstancesInfoRepository instancesInfoRepository;

    @Inject
    private InstancesDataService instancesDataService;


    @Override
    public void deleteAll() {
        instancesInfoRepository.deleteAll();
    }

    @Override
    public void init() {
        saveInstancesInfoData();
        when(filterTemplateService.getGlobalFilterFields(INSTANCES_INFO)).thenReturn(
                Collections.singletonList(InstancesInfo_.RELATION_NAME));
    }

    @Test
    void testGlobalSearch() {
        var pageRequestDto = new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, InstancesInfo_.CREATED_DATE, false,
                RELATION_1,
                Collections.emptyList());
        var instancesInfoPage = instancesDataService.getNextPage(pageRequestDto);
        assertThat(instancesInfoPage).isNotNull();
        assertThat(instancesInfoPage.getPage()).isZero();
        assertThat(instancesInfoPage.getTotalCount()).isOne();
    }

    public void saveInstancesInfoData() {
        var instancesInfo1= buildInstancesInfo();
        instancesInfo1.setRelationName(RELATION_1);
        instancesInfo1.setDataMd5Hash(DigestUtils.md5Hex(RELATION_1));
        var instancesInfo2= buildInstancesInfo();
        instancesInfo2.setRelationName(RELATION_2);
        instancesInfo2.setDataMd5Hash(DigestUtils.md5Hex(RELATION_2));
        instancesInfoRepository.saveAll(Arrays.asList(instancesInfo1, instancesInfo2));
    }
}
