package com.ecaservice.service.ers;

import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.web.dto.FilterRequestDto;
import com.ecaservice.web.dto.FilterType;
import com.ecaservice.web.dto.MatchMode;
import com.ecaservice.web.dto.PageRequestDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link ClassifierOptionsRequestService} functionality.
 *
 * @author Roman Batygin
 */
@Import(ClassifierOptionsRequestService.class)
public class ClassifierOptionsRequestServiceTest extends AbstractJpaTest {

    @Inject
    private ClassifierOptionsRequestService classifierOptionsRequestService;
    @Inject
    private ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;

    @Before
    public void init() {
        classifierOptionsRequestModelRepository.deleteAll();
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
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, "requestId", false, new ArrayList<>());
        pageRequestDto.getFilters().add(new FilterRequestDto("requestId", requestModel2.getRequestId(), FilterType.TEXT,
                MatchMode.EQUALS));
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
