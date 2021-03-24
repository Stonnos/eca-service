package com.ecaservice.ers.mapping;

import com.ecaservice.ers.dto.ClassificationCostsReport;
import com.ecaservice.ers.dto.RocCurveReport;
import com.ecaservice.ers.model.ClassificationCostsInfo;
import com.ecaservice.ers.model.RocCurveInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.ers.TestHelperUtils.buildClassificationCostsInfo;
import static com.ecaservice.ers.TestHelperUtils.buildClassificationCostsReport;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link ClassificationCostsReportMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({ClassificationCostsReportMapperImpl.class, RocCurveReportMapperImpl.class})
class ClassificationCostsReportMapperTest {

    @Inject
    private ClassificationCostsReportMapper costsReportMapper;

    @Test
    void testMapClassificationCostsReport() {
        ClassificationCostsReport costsReport = buildClassificationCostsReport();
        ClassificationCostsInfo classificationCostsInfo = costsReportMapper.map(costsReport);
        assertThat(classificationCostsInfo.getClassValue()).isEqualTo(costsReport.getClassValue());
        RocCurveInfo rocCurveInfo = classificationCostsInfo.getRocCurveInfo();
        assertThat(rocCurveInfo.getAucValue()).isEqualTo(costsReport.getRocCurve().getAucValue());
        assertThat(rocCurveInfo.getSpecificity()).isEqualTo(costsReport.getRocCurve().getSpecificity());
        assertThat(rocCurveInfo.getSensitivity()).isEqualTo(costsReport.getRocCurve().getSensitivity());
        assertThat(rocCurveInfo.getThresholdValue()).isEqualTo(
                costsReport.getRocCurve().getThresholdValue());
        assertThat(classificationCostsInfo.getFalseNegativeRate()).isEqualTo(
                costsReport.getFalseNegativeRate());
        assertThat(classificationCostsInfo.getFalsePositiveRate()).isEqualTo(
                costsReport.getFalsePositiveRate());
        assertThat(classificationCostsInfo.getTrueNegativeRate()).isEqualTo(
                costsReport.getTrueNegativeRate());
        assertThat(classificationCostsInfo.getTruePositiveRate()).isEqualTo(
                costsReport.getTruePositiveRate());
    }

    @Test
    void testMapClassificationCostsInfo() {
        ClassificationCostsInfo classificationCostsInfo = buildClassificationCostsInfo();
        ClassificationCostsReport costsReport = costsReportMapper.map(classificationCostsInfo);
        assertThat(costsReport.getClassValue()).isEqualTo(classificationCostsInfo.getClassValue());
        RocCurveReport rocCurveReport = costsReport.getRocCurve();
        assertThat(rocCurveReport.getAucValue()).isEqualTo(
                classificationCostsInfo.getRocCurveInfo().getAucValue());
        assertThat(rocCurveReport.getSpecificity()).isEqualTo(
                classificationCostsInfo.getRocCurveInfo().getSpecificity());
        assertThat(rocCurveReport.getSensitivity()).isEqualTo(
                classificationCostsInfo.getRocCurveInfo().getSensitivity());
        assertThat(rocCurveReport.getThresholdValue()).isEqualTo(
                classificationCostsInfo.getRocCurveInfo().getThresholdValue());
        assertThat(costsReport.getFalseNegativeRate()).isEqualTo(
                classificationCostsInfo.getFalseNegativeRate());
        assertThat(costsReport.getFalsePositiveRate()).isEqualTo(
                classificationCostsInfo.getFalsePositiveRate());
        assertThat(costsReport.getTrueNegativeRate()).isEqualTo(
                classificationCostsInfo.getTrueNegativeRate());
        assertThat(costsReport.getTruePositiveRate()).isEqualTo(
                classificationCostsInfo.getTruePositiveRate());
    }
}
