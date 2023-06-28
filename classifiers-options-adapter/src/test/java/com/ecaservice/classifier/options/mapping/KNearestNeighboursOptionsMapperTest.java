package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.TestHelperUtils;
import com.ecaservice.classifier.options.model.KNearestNeighboursOptions;
import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.DistanceBuilder;
import eca.metrics.distances.DistanceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for checking {@link KNearestNeighboursOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(KNearestNeighboursOptionsMapperImpl.class)
class KNearestNeighboursOptionsMapperTest {

    @Inject
    private KNearestNeighboursOptionsMapper kNearestNeighboursOptionsMapper;

    @Test
    void testMapKNearestNeighboursOptions() {
        KNearestNeighboursOptions kNearestNeighboursOptions = TestHelperUtils.createKNearestNeighboursOptions();
        DistanceBuilder distanceBuilder = new DistanceBuilder();
        for (DistanceType distanceType : DistanceType.values()) {
            kNearestNeighboursOptions.setDistanceType(distanceType);
            assertThat(kNearestNeighboursOptionsMapper.map(kNearestNeighboursOptions).getDistance()).isInstanceOf(
                    distanceType.handle(distanceBuilder).getClass());
        }
        KNearestNeighbours kNearestNeighbours = kNearestNeighboursOptionsMapper.map(kNearestNeighboursOptions);
        assertThat(kNearestNeighbours.getNumNeighbours()).isEqualTo(kNearestNeighboursOptions.getNumNeighbours());
        assertThat(kNearestNeighbours.getWeight()).isEqualTo(kNearestNeighboursOptions.getWeight());
    }
}
