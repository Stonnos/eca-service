package com.ecaservice.ers.mapping;

import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.ers.model.ClassifierOptionsInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.ers.TestHelperUtils.buildClassifierReport;
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
        ClassifierReport classifierReport = buildClassifierReport();
        ClassifierOptionsInfo classifierOptionsInfo = classifierReportMapper.map(classifierReport);
        assertThat(classifierOptionsInfo.getClassifierName()).isEqualTo(
                classifierReport.getClassifierName());
        assertThat(classifierOptionsInfo.getOptions()).isEqualTo(
                classifierReport.getOptions());
        assertThat(classifierOptionsInfo.getClassifierDescription()).isEqualTo(
                classifierReport.getClassifierDescription());
    }
}
