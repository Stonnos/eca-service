package com.ecaservice.report;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CommonConfig;
import com.ecaservice.mapping.ClassifierOptionsRequestModelMapperImpl;
import com.ecaservice.mapping.ClassifierOptionsResponseModelMapperImpl;
import com.ecaservice.mapping.DateTimeConverter;
import com.ecaservice.mapping.ErsEvaluationMethodMapperImpl;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.ErsRequest_;
import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.report.model.ClassifierOptionsRequestBean;
import com.ecaservice.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.ers.ClassifierOptionsRequestService;
import com.ecaservice.core.filter.service.FilterService;
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

import static com.ecaservice.AssertionUtils.assertBaseReportBean;
import static com.ecaservice.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.PageRequestUtils.PAGE_SIZE;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Unit tests that checks EvaluationLogsBaseReportDataFetcher functionality {@see EvaluationLogsBaseReportDataFetcher}.
 *
 * @author Roman Batygin
 */
@Import({CommonConfig.class, ClassifierOptionsRequestModelMapperImpl.class, DateTimeConverter.class,
        ClassifierOptionsRequestsBaseReportDataFetcher.class, ErsEvaluationMethodMapperImpl.class,
        ClassifierOptionsResponseModelMapperImpl.class, ClassifierOptionsRequestService.class})
class ClassifierOptionsRequestsBaseReportDataFetcherTest extends AbstractJpaTest {

    private static final String DATA_MD_5_HASH = "dataMd5";

    @MockBean
    private FilterService filterService;

    @Inject
    private ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;

    @Inject
    private ClassifierOptionsRequestsBaseReportDataFetcher classifierOptionsRequestsBaseReportDataFetcher;

    @Override
    public void deleteAll() {
        classifierOptionsRequestModelRepository.deleteAll();
    }

    @Test
    void testFetchClassifierOptionsRequestsData() {
        ClassifierOptionsRequestModel requestModel =
                TestHelperUtils.createClassifierOptionsRequestModel(DATA_MD_5_HASH, LocalDateTime.now(),
                        ErsResponseStatus.SUCCESS, Collections.emptyList());
        classifierOptionsRequestModelRepository.save(requestModel);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, ErsRequest_.REQUEST_DATE, false, StringUtils.EMPTY,
                        newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(ErsRequest_.REQUEST_ID, Collections.singletonList(requestModel.getRequestId()),
                        MatchMode.LIKE));
        BaseReportBean<ClassifierOptionsRequestBean> baseReportBean =
                classifierOptionsRequestsBaseReportDataFetcher.fetchReportData(pageRequestDto);
        assertBaseReportBean(baseReportBean, pageRequestDto);
    }
}
