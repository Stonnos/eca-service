package com.ecaservice.server.service.ers;

import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.server.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.server.mapping.ClassifierOptionsRequestModelMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel_;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.classifiers.ClassifierOptionsProcessor;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createClassifierOptionsRequestModel;
import static com.ecaservice.server.TestHelperUtils.createInstancesInfo;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ClassifierOptionsRequestService} functionality.
 *
 * @author Roman Batygin
 */
@Import({ClassifierOptionsRequestModelMapperImpl.class, DateTimeConverter.class, InstancesInfoMapperImpl.class})
class ClassifierOptionsRequestServiceTest extends AbstractJpaTest {

    private static final String INSTANCES_INFO_RELATION_NAME = "instancesInfo.relationName";
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;

    @Inject
    private ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;
    @Inject
    private InstancesInfoRepository instancesInfoRepository;
    @Inject
    private ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;
    @Mock
    private FilterService filterService;
    @Mock
    private ClassifierOptionsProcessor classifierOptionsProcessor;

    private ClassifierOptionsRequestService classifierOptionsRequestService;

    private InstancesInfo instancesInfo;

    @Override
    public void init() {
        classifierOptionsRequestService = new ClassifierOptionsRequestService(filterService,
                classifierOptionsProcessor, classifierOptionsRequestModelMapper,
                classifierOptionsRequestModelRepository);
        instancesInfo = createInstancesInfo();
        instancesInfoRepository.save(instancesInfo);
    }

    @Override
    public void deleteAll() {
        classifierOptionsRequestModelRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    /**
     * Tests global filtering relationName search query and response status equals to SUCCESS.
     */
    @Test
    void testGlobalFilter() {
        ClassifierOptionsRequestModel requestModel =
                createClassifierOptionsRequestModel(instancesInfo, LocalDateTime.now(),
                        ErsResponseStatus.SUCCESS, Collections.emptyList());
        InstancesInfo anotherInstancesInfo = createInstancesInfo();
        anotherInstancesInfo.setDataMd5Hash("anotherHash");
        anotherInstancesInfo.setRelationName("anotherData");
        instancesInfoRepository.save(anotherInstancesInfo);
        ClassifierOptionsRequestModel requestModel1 =
                createClassifierOptionsRequestModel(anotherInstancesInfo, LocalDateTime.now(),
                        ErsResponseStatus.SUCCESS, Collections.emptyList());
        classifierOptionsRequestModelRepository.saveAll(Arrays.asList(requestModel, requestModel1));
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, ClassifierOptionsRequestModel_.REQUEST_DATE, false,
                        instancesInfo.getRelationName(),
                        newArrayList());
        pageRequestDto.getFilters().add(new FilterRequestDto(ClassifierOptionsRequestModel_.RESPONSE_STATUS,
                Collections.singletonList(ErsResponseStatus.SUCCESS.name()), MatchMode.EQUALS));
        when(filterService.getGlobalFilterFields(FilterTemplateType.CLASSIFIER_OPTIONS_REQUEST.name())).thenReturn(
                Arrays.asList(INSTANCES_INFO_RELATION_NAME, ClassifierOptionsRequestModel_.REQUEST_ID));
        var classifierOptionsRequestDtoPage =
                classifierOptionsRequestService.getClassifierOptionsRequestsPage(pageRequestDto);
        assertThat(classifierOptionsRequestDtoPage).isNotNull();
        assertThat(classifierOptionsRequestDtoPage.getTotalCount()).isOne();
    }

    @Test
    void testFilterByRequestId() {
        ClassifierOptionsRequestModel requestModel =
                createClassifierOptionsRequestModel(instancesInfo, LocalDateTime.now(),
                        ErsResponseStatus.SUCCESS, Collections.emptyList());
        classifierOptionsRequestModelRepository.save(requestModel);
        ClassifierOptionsRequestModel requestModel2 =
                createClassifierOptionsRequestModel(instancesInfo, LocalDateTime.now(),
                        ErsResponseStatus.SUCCESS, Collections.emptyList());
        classifierOptionsRequestModelRepository.save(requestModel2);
        ClassifierOptionsRequestModel requestModel3 =
                createClassifierOptionsRequestModel(instancesInfo, LocalDateTime.now(),
                        ErsResponseStatus.SUCCESS, Collections.emptyList());
        classifierOptionsRequestModelRepository.save(requestModel3);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, ClassifierOptionsRequestModel_.REQUEST_ID, false, null,
                        newArrayList());
        pageRequestDto.getFilters().add(new FilterRequestDto(ClassifierOptionsRequestModel_.REQUEST_ID,
                Collections.singletonList(requestModel2.getRequestId()), MatchMode.EQUALS));
        var classifierOptionsRequestDtoPage =
                classifierOptionsRequestService.getClassifierOptionsRequestsPage(pageRequestDto);
        var classifierOptionsRequestDtoList = classifierOptionsRequestDtoPage.getContent();
        assertThat(classifierOptionsRequestDtoPage).isNotNull();
        assertThat(classifierOptionsRequestDtoPage.getTotalCount()).isOne();
        assertThat(classifierOptionsRequestDtoList.iterator().next().getRequestId()).isEqualTo(
                requestModel2.getRequestId());
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
                createClassifierOptionsRequestModel(instancesInfo, LocalDateTime.now(),
                        ErsResponseStatus.ERROR, Collections.emptyList());
        requestModel.setRequestId(UUID.randomUUID().toString());
        classifierOptionsRequestModelRepository.save(requestModel);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, ClassifierOptionsRequestModel_.REQUEST_DATE, false, null,
                        newArrayList());
        pageRequestDto.getFilters().add(new FilterRequestDto(ClassifierOptionsRequestModel_.REQUEST_ID,
                Arrays.asList(requestModel.getRequestId(), requestModel.getRequestId()), MatchMode.RANGE));
        var classifierOptionsRequestDtoPage =
                classifierOptionsRequestService.getClassifierOptionsRequestsPage(pageRequestDto);
        assertThat(classifierOptionsRequestDtoPage).isNotNull();
        assertThat(classifierOptionsRequestDtoPage.getTotalCount()).isOne();
    }

    @Test
    void testEqualsFilterByDateField() {
        ClassifierOptionsRequestModel requestModel =
                createClassifierOptionsRequestModel(instancesInfo, LocalDateTime.now(),
                        ErsResponseStatus.ERROR, Collections.emptyList());
        classifierOptionsRequestModelRepository.save(requestModel);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, ClassifierOptionsRequestModel_.REQUEST_DATE, false, null,
                        newArrayList());
        pageRequestDto.getFilters().add(new FilterRequestDto(ClassifierOptionsRequestModel_.REQUEST_DATE,
                Collections.singletonList(dateTimeFormatter.format(requestModel.getRequestDate())), MatchMode.EQUALS));
        var classifierOptionsRequestDtoPage =
                classifierOptionsRequestService.getClassifierOptionsRequestsPage(pageRequestDto);
        assertThat(classifierOptionsRequestDtoPage).isNotNull();
        assertThat(classifierOptionsRequestDtoPage.getTotalCount()).isOne();
    }

    @Test
    void testLikeFilterForNotStringField() {
        FilterRequestDto filterRequestDto = new FilterRequestDto(ClassifierOptionsRequestModel_.REQUEST_DATE,
                Collections.singletonList("2018-11-11"), MatchMode.LIKE);
        assertThrows(InvalidDataAccessApiUsageException.class, () -> testFilterForIllegalFieldType(filterRequestDto));
    }

    @Test
    void testGlobalFilterForNotStringField() {
        when(filterService.getGlobalFilterFields(FilterTemplateType.CLASSIFIER_OPTIONS_REQUEST.name())).thenReturn(
                Collections.singletonList(ClassifierOptionsRequestModel_.REQUEST_DATE));
        ClassifierOptionsRequestModel requestModel =
                createClassifierOptionsRequestModel(instancesInfo, LocalDateTime.now(),
                        ErsResponseStatus.ERROR, Collections.emptyList());
        classifierOptionsRequestModelRepository.save(requestModel);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, ClassifierOptionsRequestModel_.REQUEST_DATE, false, "query",
                        newArrayList());
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> classifierOptionsRequestService.getClassifierOptionsRequestsPage(pageRequestDto));
    }

    private void testFilterForIllegalFieldType(FilterRequestDto filterRequestDto) {
        ClassifierOptionsRequestModel requestModel =
                createClassifierOptionsRequestModel(instancesInfo, LocalDateTime.now(),
                        ErsResponseStatus.ERROR, Collections.emptyList());
        classifierOptionsRequestModelRepository.save(requestModel);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, ClassifierOptionsRequestModel_.REQUEST_DATE, false, null,
                        newArrayList());
        pageRequestDto.getFilters().add(filterRequestDto);
        classifierOptionsRequestService.getClassifierOptionsRequestsPage(pageRequestDto);
    }
}
