package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.ClassifierReport;
import com.ecaservice.model.entity.ClassifierOptionsResponseModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(classifierOptionsResponseModel.getClassifierName()).isEqualTo(
                classifierReport.getClassifierName());
        assertThat(classifierOptionsResponseModel.getClassifierDescription()).isEqualTo(
                classifierReport.getClassifierDescription());
        assertThat(classifierOptionsResponseModel.getOptions()).isEqualTo(classifierReport.getOptions());
    }

    @Test
    public void testMapClassifierReportList() {
        ClassifierReport classifierReport = TestHelperUtils.createClassifierReport();
        ClassifierReport classifierReport1 = TestHelperUtils.createClassifierReport();
        List<ClassifierOptionsResponseModel> classifierOptionsRequestModelList =
                classifierReportMapper.map(Arrays.asList
                        (classifierReport, classifierReport1));
        assertThat(classifierOptionsRequestModelList).isNotNull();
        assertThat(classifierOptionsRequestModelList).hasSize(2);
    }
}
