package com.ecaservice.service.ers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CommonConfig;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel_;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.filter.GlobalFilterService;
import com.ecaservice.web.dto.model.FilterFieldType;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ClassifierOptionsRequestService} functionality.
 *
 * @author Roman Batygin
 */
@Import(CommonConfig.class)
public class ClassifierOptionsRequestServiceTest extends AbstractJpaTest {

    @Inject
    private ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;
    @Inject
    private CommonConfig commonConfig;
    @Mock
    private GlobalFilterService globalFilterService;


    private ClassifierOptionsRequestService classifierOptionsRequestService;

    @Override
    public void init() {
        classifierOptionsRequestService = new ClassifierOptionsRequestService(commonConfig, globalFilterService,
                classifierOptionsRequestModelRepository);
    }

    @Override
    public void deleteAll() {
        classifierOptionsRequestModelRepository.deleteAll();
    }

    /**
     * Tests global filtering relationName "gla" search query and response status equals to SUCCESS.
     */
    @Test
    public void testGlobalFilter() {
        ClassifierOptionsRequestModel requestModel =
                TestHelperUtils.createClassifierOptionsRequestModel(StringUtils.EMPTY, LocalDateTime.now(),
                        ResponseStatus.ERROR, Collections.emptyList());
        requestModel.setRelationName("relation1");
        ClassifierOptionsRequestModel requestModel1 =
                TestHelperUtils.createClassifierOptionsRequestModel(StringUtils.EMPTY, LocalDateTime.now(),
                        ResponseStatus.SUCCESS, Collections.emptyList());
        requestModel1.setRelationName("relation2");
        ClassifierOptionsRequestModel requestModel2 =
                TestHelperUtils.createClassifierOptionsRequestModel(StringUtils.EMPTY, LocalDateTime.now(),
                        ResponseStatus.SUCCESS, Collections.emptyList());
        requestModel2.setRelationName("glass");
        ClassifierOptionsRequestModel requestModel3 =
                TestHelperUtils.createClassifierOptionsRequestModel(StringUtils.EMPTY, LocalDateTime.now(),
                        ResponseStatus.RESULTS_NOT_FOUND, Collections.emptyList());
        requestModel3.setRelationName("glass");
        classifierOptionsRequestModelRepository.saveAll(
                Arrays.asList(requestModel, requestModel1, requestModel2, requestModel3));
        PageRequestDto pageRequestDto =
                new PageRequestDto(0, 10, ClassifierOptionsRequestModel_.REQUEST_DATE, false, "gla", newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(ClassifierOptionsRequestModel_.RESPONSE_STATUS, ResponseStatus.SUCCESS.name(),
                        FilterFieldType.REFERENCE, MatchMode.EQUALS));
        when(globalFilterService.getGlobalFilterFields(FilterTemplateType.CLASSIFIER_OPTIONS_REQUEST)).thenReturn(
                Arrays.asList(ClassifierOptionsRequestModel_.RELATION_NAME, ClassifierOptionsRequestModel_.REQUEST_ID));
        Page<ClassifierOptionsRequestModel> classifierOptionsRequestModelPage =
                classifierOptionsRequestService.getNextPage(pageRequestDto);
        assertThat(classifierOptionsRequestModelPage).isNotNull();
        assertThat(classifierOptionsRequestModelPage.getTotalElements()).isOne();
    }

    @Test
    public void testFilterByRequestId() {
        ClassifierOptionsRequestModel requestModel = new ClassifierOptionsRequestModel();
        requestModel.setRequestId(UUID.randomUUID().toString());
        classifierOptionsRequestModelRepository.save(requestModel);
        ClassifierOptionsRequestModel requestModel2 = new ClassifierOptionsRequestModel();
        requestModel2.setRequestId(UUID.randomUUID().toString());
        classifierOptionsRequestModelRepository.save(requestModel2);
        ClassifierOptionsRequestModel requestModel3 = new ClassifierOptionsRequestModel();
        requestModel3.setRequestId(UUID.randomUUID().toString());
        classifierOptionsRequestModelRepository.save(requestModel3);
        PageRequestDto pageRequestDto =
                new PageRequestDto(0, 10, ClassifierOptionsRequestModel_.REQUEST_ID, false, null, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(ClassifierOptionsRequestModel_.REQUEST_ID, requestModel2.getRequestId(),
                        FilterFieldType.TEXT, MatchMode.EQUALS));
        Page<ClassifierOptionsRequestModel> classifierOptionsRequestModelPage =
                classifierOptionsRequestService.getNextPage(pageRequestDto);
        List<ClassifierOptionsRequestModel> classifierOptionsRequestModels =
                classifierOptionsRequestModelPage.getContent();
        assertThat(classifierOptionsRequestModelPage).isNotNull();
        assertThat(classifierOptionsRequestModelPage.getTotalElements()).isOne();
        assertThat(classifierOptionsRequestModels.size()).isOne();
        assertThat(classifierOptionsRequestModels.get(0).getRequestId()).isEqualTo(requestModel2.getRequestId());
    }
}
