package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.RocCurvePoint;
import com.ecaservice.web.dto.model.RocCurvePointDto;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests that checks {@link RocCurvePointMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(RocCurvePointMapperImpl.class)
public class RocCurvePointMapperTest {

    @Inject
    private RocCurvePointMapper rocCurvePointMapper;

    @Test
    public void testMapRocCurvePoint() {
        RocCurvePoint rocCurvePoint = TestHelperUtils.createRocCurvePoint();
        RocCurvePointDto rocCurvePointDto = rocCurvePointMapper.map(rocCurvePoint);
        Assertions.assertThat(rocCurvePointDto).isNotNull();
        Assertions.assertThat(rocCurvePointDto.getXValue()).isEqualTo(rocCurvePoint.getXValue());
        Assertions.assertThat(rocCurvePointDto.getYValue()).isEqualTo(rocCurvePoint.getYValue());
    }
}
