package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.ClassificationCostsReport;
import com.ecaservice.web.dto.model.ClassificationCostsDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

/**
 * Unit tests that checks {@link ClassificationCostsMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(ClassificationCostsMapperImpl.class)
class ClassificationCostsMapperTest {

    @Inject
    private ClassificationCostsMapper classificationCostsMapper;

    @Test
    void testMapClassificationCosts() {
        ClassificationCostsReport classificationCostsReport = TestHelperUtils.createClassificationCostsReport();
        ClassificationCostsDto classificationCostsDto = classificationCostsMapper.map(classificationCostsReport);
        Assertions.assertThat(classificationCostsDto).isNotNull();
        Assertions.assertThat(classificationCostsDto.getClassValue()).isEqualTo(
                classificationCostsReport.getClassValue());
        Assertions.assertThat(classificationCostsDto.getAucValue()).isEqualTo(
                classificationCostsReport.getRocCurve().getAucValue());
        Assertions.assertThat(classificationCostsDto.getTrueNegativeRate()).isEqualTo(
                classificationCostsReport.getTrueNegativeRate());
        Assertions.assertThat(classificationCostsDto.getTruePositiveRate()).isEqualTo(
                classificationCostsReport.getTruePositiveRate());
        Assertions.assertThat(classificationCostsDto.getFalseNegativeRate()).isEqualTo(
                classificationCostsReport.getFalseNegativeRate());
        Assertions.assertThat(classificationCostsDto.getFalsePositiveRate()).isEqualTo(
                classificationCostsReport.getFalsePositiveRate());
    }
}
