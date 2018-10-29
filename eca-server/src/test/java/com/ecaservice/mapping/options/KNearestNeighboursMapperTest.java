package com.ecaservice.mapping.options;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.options.KNearestNeighboursOptions;
import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.ManhattanDistance;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link KNearestNeighboursMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(KNearestNeighboursMapperImpl.class)
public class KNearestNeighboursMapperTest {

    @Inject
    private KNearestNeighboursMapper kNearestNeighboursMapper;

    @Test
    public void testMapKnn() {
        KNearestNeighbours kNearestNeighbours = TestHelperUtils.createKNearestNeighbours(new ManhattanDistance());
        KNearestNeighboursOptions options = kNearestNeighboursMapper.map(kNearestNeighbours);
        Assertions.assertThat(options.getWeight()).isEqualTo(kNearestNeighbours.getWeight());
        Assertions.assertThat(options.getNumNeighbours()).isEqualTo(kNearestNeighbours.getNumNeighbours());
        Assertions.assertThat(options.getDistanceType()).isEqualTo(kNearestNeighbours.getDistance().getDistanceType());
    }
}
