package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.ClassifierReport;
import com.ecaservice.model.entity.ClassifierOptionsResponseModel;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link ClassifierReportMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(ClassifierReportMapperImpl.class)
public class ClassifierReportMapperTest {

    @Inject
    private ClassifierReportMapper classifierReportMapper;

    @Test
    public void testMapClassifierReport() {
        ClassifierReport classifierReport = TestHelperUtils.createClassifierReport();
        ClassifierOptionsResponseModel classifierOptionsResponseModel = classifierReportMapper.map(classifierReport);
        Assertions.assertThat(classifierOptionsResponseModel.getClassifierName()).isEqualTo(
                classifierReport.getClassifierName());
        Assertions.assertThat(classifierOptionsResponseModel.getClassifierDescription()).isEqualTo(
                classifierReport.getClassifierDescription());
        Assertions.assertThat(classifierOptionsResponseModel.getOptions()).isEqualTo(classifierReport.getOptions());
    }
}
