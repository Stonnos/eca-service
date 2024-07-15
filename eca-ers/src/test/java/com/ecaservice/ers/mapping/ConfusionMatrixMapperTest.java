package com.ecaservice.ers.mapping;

import com.ecaservice.ers.dto.ConfusionMatrixReport;
import com.ecaservice.ers.model.ConfusionMatrix;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.ecaservice.ers.TestHelperUtils.buildConfusionMatrix;
import static com.ecaservice.ers.TestHelperUtils.buildConfusionMatrixReport;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link ConfusionMatrixMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(ConfusionMatrixMapperImpl.class)
class ConfusionMatrixMapperTest {

    @Autowired
    private ConfusionMatrixMapper confusionMatrixMapper;

    @Test
    void testMapConfusionMatrixReport() {
        ConfusionMatrixReport report = buildConfusionMatrixReport();
        ConfusionMatrix confusionMatrix = confusionMatrixMapper.map(report);
        assertThat(confusionMatrix.getActualClassIndex()).isEqualTo(report.getActualClassIndex());
        assertThat(confusionMatrix.getPredictedClassIndex()).isEqualTo(report.getPredictedClassIndex());
        assertThat(confusionMatrix.getNumInstances().intValue()).isEqualTo(
                report.getNumInstances().intValue());
    }

    @Test
    void testMapConfusionMatrix() {
        ConfusionMatrix confusionMatrix = buildConfusionMatrix();
        ConfusionMatrixReport confusionMatrixReport = confusionMatrixMapper.map(confusionMatrix);
        assertThat(confusionMatrixReport.getActualClassIndex()).isEqualTo(confusionMatrix.getActualClassIndex());
        assertThat(confusionMatrixReport.getPredictedClassIndex()).isEqualTo(confusionMatrix.getPredictedClassIndex());
        assertThat(confusionMatrixReport.getNumInstances().intValue()).isEqualTo(
                confusionMatrix.getNumInstances().intValue());
    }
}
