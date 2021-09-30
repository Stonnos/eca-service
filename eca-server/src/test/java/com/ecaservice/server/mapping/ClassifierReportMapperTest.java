package com.ecaservice.server.mapping;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.server.model.entity.ClassifierOptionsResponseModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link ClassifierReportMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(ClassifierReportMapperImpl.class)
class ClassifierReportMapperTest {

    @Inject
    private ClassifierReportMapper classifierReportMapper;

    @Test
    void testMapClassifierReport() {
        ClassifierReport classifierReport = TestHelperUtils.createClassifierReport();
        ClassifierOptionsResponseModel classifierOptionsResponseModel = classifierReportMapper.map(classifierReport);
        assertThat(classifierOptionsResponseModel.getClassifierName()).isEqualTo(
                classifierReport.getClassifierName());
        assertThat(classifierOptionsResponseModel.getClassifierDescription()).isEqualTo(
                classifierReport.getClassifierDescription());
        assertThat(classifierOptionsResponseModel.getOptions()).isEqualTo(classifierReport.getOptions());
    }

    @Test
    void testMapClassifierReportList() {
        ClassifierReport classifierReport = TestHelperUtils.createClassifierReport();
        ClassifierReport classifierReport1 = TestHelperUtils.createClassifierReport();
        List<ClassifierOptionsResponseModel> classifierOptionsRequestModelList =
                classifierReportMapper.map(Arrays.asList
                        (classifierReport, classifierReport1));
        assertThat(classifierOptionsRequestModelList).hasSize(2);
    }
}
