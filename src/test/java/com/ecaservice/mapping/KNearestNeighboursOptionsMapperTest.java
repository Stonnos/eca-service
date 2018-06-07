package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.options.KNearestNeighboursOptions;
import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.DistanceBuilder;
import eca.metrics.distances.DistanceType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for checking {@link KNearestNeighboursOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class KNearestNeighboursOptionsMapperTest {

    @Inject
    private KNearestNeighboursOptionsMapper kNearestNeighboursOptionsMapper;
    @Inject
    private ExperimentConfig experimentConfig;

    @Test
    public void testMapKNearestNeighboursOptions() {
        KNearestNeighboursOptions kNearestNeighboursOptions = TestHelperUtils.createKNearestNeighboursOptions();
        DistanceBuilder distanceBuilder = new DistanceBuilder();
        for (DistanceType distanceType : DistanceType.values()) {
            kNearestNeighboursOptions.setDistanceType(distanceType);
            assertThat(kNearestNeighboursOptionsMapper.map(kNearestNeighboursOptions).getDistance()).isInstanceOf(
                    distanceType.handle(distanceBuilder).getClass());
        }
        KNearestNeighbours kNearestNeighbours = kNearestNeighboursOptionsMapper.map(kNearestNeighboursOptions);
        assertThat(kNearestNeighbours.getDecimalFormat().getMaximumFractionDigits()).isEqualTo(
                experimentConfig.getMaximumFractionDigits());
        assertThat(kNearestNeighbours.getNumNeighbours()).isEqualTo(kNearestNeighboursOptions.getNumNeighbours());
        assertThat(kNearestNeighbours.getWeight()).isEqualTo(kNearestNeighboursOptions.getWeight());
    }
}
