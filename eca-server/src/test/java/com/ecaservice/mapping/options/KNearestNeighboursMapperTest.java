package com.ecaservice.mapping.options;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.options.KNearestNeighboursOptions;
import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.ManhattanDistance;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link KNearestNeighboursMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(KNearestNeighboursMapperImpl.class)
public class KNearestNeighboursMapperTest {

    @Inject
    private KNearestNeighboursMapper kNearestNeighboursMapper;

    @Test
    void testMapKnn() {
        KNearestNeighbours kNearestNeighbours = TestHelperUtils.createKNearestNeighbours(new ManhattanDistance());
        KNearestNeighboursOptions options = kNearestNeighboursMapper.map(kNearestNeighbours);
        Assertions.assertThat(options.getWeight()).isEqualTo(kNearestNeighbours.getWeight());
        Assertions.assertThat(options.getNumNeighbours()).isEqualTo(kNearestNeighbours.getNumNeighbours());
        Assertions.assertThat(options.getDistanceType()).isEqualTo(kNearestNeighbours.getDistance().getDistanceType());
    }
}
