package com.ecaservice.service.ers;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CommonConfig;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel_;
import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.filter.FilterService;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ClassifierOptionsRequestService} functionality.
 *
 * @author Roman Batygin
 */
@Import(CommonConfig.class)
public class ClassifierOptionsRequestServiceTest extends AbstractJpaTest {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;

    @Inject
    private ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;
    @Inject
    private CommonConfig commonConfig;
    @Mock
    private FilterService filterService;


    private ClassifierOptionsRequestService classifierOptionsRequestService;

    @Override
    public void init() {
        classifierOptionsRequestService = new ClassifierOptionsRequestService(commonConfig, filterService,
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
    void testGlobalFilter() {
        ClassifierOptionsRequestModel requestModel =
                TestHelperUtils.createClassifierOptionsRequestModel(StringUtils.EMPTY, LocalDateTime.now(),
                        ErsResponseStatus.ERROR, Collections.emptyList());
        requestModel.setRelationName("relation1");
        ClassifierOptionsRequestModel requestModel1 =
                TestHelperUtils.createClassifierOptionsRequestModel(StringUtils.EMPTY, LocalDateTime.now(),
                        ErsResponseStatus.SUCCESS, Collections.emptyList());
        requestModel1.setRelationName("relation2");
        ClassifierOptionsRequestModel requestModel2 =
                TestHelperUtils.createClassifierOptionsRequestModel(StringUtils.EMPTY, LocalDateTime.now(),
                        ErsResponseStatus.SUCCESS, Collections.emptyList());
        requestModel2.setRelationName("glass");
        ClassifierOptionsRequestModel requestModel3 =
                TestHelperUtils.createClassifierOptionsRequestModel(StringUtils.EMPTY, LocalDateTime.now(),
                        ErsResponseStatus.RESULTS_NOT_FOUND, Collections.emptyList());
        requestModel3.setRelationName("glass");
        classifierOptionsRequestModelRepository.saveAll(
                Arrays.asList(requestModel, requestModel1, requestModel2, requestModel3));
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, ClassifierOptionsRequestModel_.REQUEST_DATE, false, "gla",
                        newArrayList());
        pageRequestDto.getFilters().add(new FilterRequestDto(ClassifierOptionsRequestModel_.RESPONSE_STATUS,
                Collections.singletonList(ErsResponseStatus.SUCCESS.name()), MatchMode.EQUALS));
        when(filterService.getGlobalFilterFields(FilterTemplateType.CLASSIFIER_OPTIONS_REQUEST)).thenReturn(
                Arrays.asList(ClassifierOptionsRequestModel_.RELATION_NAME, ClassifierOptionsRequestModel_.REQUEST_ID));
        Page<ClassifierOptionsRequestModel> classifierOptionsRequestModelPage =
                classifierOptionsRequestService.getNextPage(pageRequestDto);
        assertThat(classifierOptionsRequestModelPage).isNotNull();
        assertThat(classifierOptionsRequestModelPage.getTotalElements()).isOne();
    }

    @Test
    void testFilterByRequestId() {
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
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, ClassifierOptionsRequestModel_.REQUEST_ID, false, null,
                        newArrayList());
        pageRequestDto.getFilters().add(new FilterRequestDto(ClassifierOptionsRequestModel_.REQUEST_ID,
                Collections.singletonList(requestModel2.getRequestId()), MatchMode.EQUALS));
        Page<ClassifierOptionsRequestModel> classifierOptionsRequestModelPage =
                classifierOptionsRequestService.getNextPage(pageRequestDto);
        List<ClassifierOptionsRequestModel> classifierOptionsRequestModels =
                classifierOptionsRequestModelPage.getContent();
        assertThat(classifierOptionsRequestModelPage).isNotNull();
        assertThat(classifierOptionsRequestModelPage.getTotalElements()).isOne();
        assertThat(classifierOptionsRequestModels.get(0).getRequestId()).isEqualTo(requestModel2.getRequestId());
    }

    @Test
    void testRangeFilterForIllegalFieldType() {
        FilterRequestDto filterRequestDto = new FilterRequestDto(ClassifierOptionsRequestModel_.RESPONSE_STATUS,
                Arrays.asList(ErsResponseStatus.RESULTS_NOT_FOUND.name(), ErsResponseStatus.ERROR.name()),
                MatchMode.RANGE);
        assertThrows(InvalidDataAccessApiUsageException.class, () -> testFilterForIllegalFieldType(filterRequestDto));
    }

    @Test
    void testRangeFilterForStringField() {
        ClassifierOptionsRequestModel requestModel =
                TestHelperUtils.createClassifierOptionsRequestModel(StringUtils.EMPTY, LocalDateTime.now(),
                        ErsResponseStatus.ERROR, Collections.emptyList());
        requestModel.setRequestId(UUID.randomUUID().toString());
        classifierOptionsRequestModelRepository.save(requestModel);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, ClassifierOptionsRequestModel_.REQUEST_DATE, false, null,
                        newArrayList());
        pageRequestDto.getFilters().add(new FilterRequestDto(ClassifierOptionsRequestModel_.REQUEST_ID,
                Arrays.asList(requestModel.getRequestId(), requestModel.getRequestId()), MatchMode.RANGE));
        Page<ClassifierOptionsRequestModel> classifierOptionsRequestModelPage =
                classifierOptionsRequestService.getNextPage(pageRequestDto);
        assertThat(classifierOptionsRequestModelPage).isNotNull();
        assertThat(classifierOptionsRequestModelPage.getTotalElements()).isOne();
    }

    @Test
    void testEqualsFilterByDateField() {
        ClassifierOptionsRequestModel requestModel =
                TestHelperUtils.createClassifierOptionsRequestModel(StringUtils.EMPTY, LocalDateTime.now(),
                        ErsResponseStatus.ERROR, Collections.emptyList());
        classifierOptionsRequestModelRepository.save(requestModel);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, ClassifierOptionsRequestModel_.REQUEST_DATE, false, null,
                        newArrayList());
        pageRequestDto.getFilters().add(new FilterRequestDto(ClassifierOptionsRequestModel_.REQUEST_DATE,
                Collections.singletonList(dateTimeFormatter.format(requestModel.getRequestDate())), MatchMode.EQUALS));
        Page<ClassifierOptionsRequestModel> classifierOptionsRequestModelPage =
                classifierOptionsRequestService.getNextPage(pageRequestDto);
        assertThat(classifierOptionsRequestModelPage).isNotNull();
        assertThat(classifierOptionsRequestModelPage.getTotalElements()).isOne();
    }

    @Test
    void testLikeFilterForNotStringField() {
        FilterRequestDto filterRequestDto = new FilterRequestDto(ClassifierOptionsRequestModel_.REQUEST_DATE,
                Collections.singletonList("2018-11-11"), MatchMode.LIKE);
        assertThrows(InvalidDataAccessApiUsageException.class, () -> testFilterForIllegalFieldType(filterRequestDto));
    }

    @Test
    void testGlobalFilterForNotStringField() {
        when(filterService.getGlobalFilterFields(FilterTemplateType.CLASSIFIER_OPTIONS_REQUEST)).thenReturn(
                Collections.singletonList(ClassifierOptionsRequestModel_.REQUEST_DATE));
        ClassifierOptionsRequestModel requestModel =
                TestHelperUtils.createClassifierOptionsRequestModel(StringUtils.EMPTY, LocalDateTime.now(),
                        ErsResponseStatus.ERROR, Collections.emptyList());
        classifierOptionsRequestModelRepository.save(requestModel);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, ClassifierOptionsRequestModel_.REQUEST_DATE, false, "query",
                        newArrayList());
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> classifierOptionsRequestService.getNextPage(pageRequestDto));
    }

    private void testFilterForIllegalFieldType(FilterRequestDto filterRequestDto) {
        ClassifierOptionsRequestModel requestModel =
                TestHelperUtils.createClassifierOptionsRequestModel(StringUtils.EMPTY, LocalDateTime.now(),
                        ErsResponseStatus.ERROR, Collections.emptyList());
        classifierOptionsRequestModelRepository.save(requestModel);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, ClassifierOptionsRequestModel_.REQUEST_DATE, false, null,
                        newArrayList());
        pageRequestDto.getFilters().add(filterRequestDto);
        classifierOptionsRequestService.getNextPage(pageRequestDto);
    }
}
