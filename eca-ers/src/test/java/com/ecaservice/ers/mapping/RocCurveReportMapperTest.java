package com.ecaservice.ers.mapping;

import com.ecaservice.ers.dto.RocCurveReport;
import com.ecaservice.ers.model.RocCurveInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.ers.TestHelperUtils.buildRocCurveInfo;
import static com.ecaservice.ers.TestHelperUtils.buildRocCurveReport;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link RocCurveReportMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(RocCurveReportMapperImpl.class)
class RocCurveReportMapperTest {

    @Inject
    private RocCurveReportMapper rocCurveReportMapper;

    @Test
    void testRocCurveReportMap() {
        RocCurveReport rocCurveReport = buildRocCurveReport();
        RocCurveInfo rocCurveInfo = rocCurveReportMapper.map(rocCurveReport);
        assertThat(rocCurveInfo.getAucValue()).isEqualTo(rocCurveReport.getAucValue());
        assertThat(rocCurveInfo.getSpecificity()).isEqualTo(rocCurveReport.getSpecificity());
        assertThat(rocCurveInfo.getSensitivity()).isEqualTo(rocCurveReport.getSensitivity());
        assertThat(rocCurveInfo.getThresholdValue()).isEqualTo(rocCurveReport.getThresholdValue());
    }

    @Test
    void testRocCurveInfoMap() {
        RocCurveInfo rocCurveInfo = buildRocCurveInfo();
        RocCurveReport rocCurveReport = rocCurveReportMapper.map(rocCurveInfo);
        assertThat(rocCurveReport.getAucValue()).isEqualTo(rocCurveInfo.getAucValue());
        assertThat(rocCurveReport.getSpecificity()).isEqualTo(rocCurveInfo.getSpecificity());
        assertThat(rocCurveReport.getSensitivity()).isEqualTo(rocCurveInfo.getSensitivity());
        assertThat(rocCurveReport.getThresholdValue()).isEqualTo(rocCurveInfo.getThresholdValue());
    }
}
