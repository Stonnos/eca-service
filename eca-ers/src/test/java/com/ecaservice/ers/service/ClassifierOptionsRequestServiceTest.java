package com.ecaservice.ers.service;

import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.ers.dto.EvaluationMethod;
import com.ecaservice.ers.exception.DataNotFoundException;
import com.ecaservice.ers.exception.ResultsNotFoundException;
import com.ecaservice.ers.mapping.ClassifierOptionsInfoMapper;
import com.ecaservice.ers.mapping.ClassifierOptionsInfoMapperImpl;
import com.ecaservice.ers.model.ClassifierOptionsInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.ers.TestHelperUtils.buildClassifierOptionsInfo;
import static com.ecaservice.ers.TestHelperUtils.createClassifierOptionsRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ClassifierOptionsRequestService} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(ClassifierOptionsInfoMapperImpl.class)
class ClassifierOptionsRequestServiceTest {

    @Inject
    private ClassifierOptionsInfoMapper classifierOptionsInfoMapper;

    @Mock
    private ClassifierOptionsService classifierOptionsService;

    private ClassifierOptionsRequestService classifierOptionsRequestService;

    @BeforeEach
    void init() {
        classifierOptionsRequestService =
                new ClassifierOptionsRequestService(classifierOptionsService, classifierOptionsInfoMapper);
    }

    @Test
    void testResultsNotFoundStatus() {
        ClassifierOptionsRequest request = createClassifierOptionsRequest(EvaluationMethod.CROSS_VALIDATION);
        when(classifierOptionsService.findBestClassifierOptions(request)).thenReturn(Collections.emptyList());
        assertThrows(ResultsNotFoundException.class,
                () -> classifierOptionsRequestService.findClassifierOptions(request));
    }

    @Test
    void testDataNotFoundStatus() {
        ClassifierOptionsRequest request = createClassifierOptionsRequest(EvaluationMethod.CROSS_VALIDATION);
        when(classifierOptionsService.findBestClassifierOptions(request)).thenThrow(
                new DataNotFoundException("Not found"));
        assertThrows(DataNotFoundException.class, () -> classifierOptionsRequestService.findClassifierOptions(request));
    }

    @Test
    void testSuccessStatus() {
        ClassifierOptionsRequest request = createClassifierOptionsRequest(EvaluationMethod.CROSS_VALIDATION);
        List<ClassifierOptionsInfo> expected = Collections.singletonList(buildClassifierOptionsInfo());
        when(classifierOptionsService.findBestClassifierOptions(request)).thenReturn(expected);
        ClassifierOptionsResponse response = classifierOptionsRequestService.findClassifierOptions(request);
        assertThat(response.getClassifierReports()).hasSameSizeAs(expected);
    }
}
