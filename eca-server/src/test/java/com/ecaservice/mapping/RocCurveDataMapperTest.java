package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.RocCurveData;
import com.ecaservice.web.dto.model.RocCurveDataDto;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests that checks {@link RocCurveDataMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import({RocCurvePointMapperImpl.class, RocCurveDataMapperImpl.class})
public class RocCurveDataMapperTest {

    @Inject
    private RocCurveDataMapper rocCurveDataMapper;

    @Test
    public void testMapRocCurveData() {
        RocCurveData rocCurveData = TestHelperUtils.createRocCurveData();
        RocCurveDataDto rocCurveDataDto = rocCurveDataMapper.map(rocCurveData);
        Assertions.assertThat(rocCurveDataDto).isNotNull();
        Assertions.assertThat(rocCurveDataDto.getClassValue()).isEqualTo(rocCurveData.getClassValue());
        Assertions.assertThat(rocCurveDataDto.getPoints()).hasSameSizeAs(rocCurveData.getPoints());
    }
}
