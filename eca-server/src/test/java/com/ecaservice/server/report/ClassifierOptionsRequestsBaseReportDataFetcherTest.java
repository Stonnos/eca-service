package com.ecaservice.server.report;

import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.mapping.ClassifierOptionsRequestModelMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.server.model.entity.ErsRequest_;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.classifiers.ClassifierOptionsProcessor;
import com.ecaservice.server.service.classifiers.ClassifiersTemplateProvider;
import com.ecaservice.server.service.ers.ClassifierOptionsRequestService;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;

import static com.ecaservice.server.AssertionUtils.assertBaseReportBean;
import static com.ecaservice.server.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.server.PageRequestUtils.PAGE_SIZE;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Unit tests that checks EvaluationLogsBaseReportDataFetcher functionality {@see EvaluationLogsBaseReportDataFetcher}.
 *
 * @author Roman Batygin
 */
@Import({AppProperties.class, ClassifierOptionsRequestModelMapperImpl.class, DateTimeConverter.class,
        InstancesInfoMapperImpl.class,
        ClassifierOptionsRequestsBaseReportDataFetcher.class, ClassifierOptionsRequestService.class})
class ClassifierOptionsRequestsBaseReportDataFetcherTest extends AbstractJpaTest {

    @MockBean
    private FilterService filterService;
    @MockBean
    private ClassifierOptionsProcessor classifierOptionsProcessor;
    @MockBean
    private ClassifiersTemplateProvider classifiersTemplateProvider;

    @Inject
    private ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;
    @Inject
    private InstancesInfoRepository instancesInfoRepository;

    @Inject
    private ClassifierOptionsRequestsBaseReportDataFetcher classifierOptionsRequestsBaseReportDataFetcher;

    @Override
    public void deleteAll() {
        classifierOptionsRequestModelRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testFetchClassifierOptionsRequestsData() {
        ClassifierOptionsRequestModel requestModel =
                TestHelperUtils.createClassifierOptionsRequestModel(TestHelperUtils.createInstancesInfo(), LocalDateTime.now(),
                        ErsResponseStatus.SUCCESS, Collections.emptyList());
        instancesInfoRepository.save(requestModel.getInstancesInfo());
        classifierOptionsRequestModelRepository.save(requestModel);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, ErsRequest_.REQUEST_DATE, false, StringUtils.EMPTY,
                        newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(ErsRequest_.REQUEST_ID, Collections.singletonList(requestModel.getRequestId()),
                        MatchMode.LIKE));
        var baseReportBean = classifierOptionsRequestsBaseReportDataFetcher.fetchReportData(pageRequestDto);
        assertBaseReportBean(baseReportBean, pageRequestDto);
    }
}
