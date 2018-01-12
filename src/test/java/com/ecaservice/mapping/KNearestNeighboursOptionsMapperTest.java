package com.ecaservice.mapping;

import com.ecaservice.model.options.KNearestNeighboursOptions;
import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.DistanceBuilder;
import eca.metrics.distances.DistanceType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for checking {@link KNearestNeighboursOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(KNearestNeighboursOptionsMapperImpl.class)
public class KNearestNeighboursOptionsMapperTest {

    @Autowired
    private KNearestNeighboursOptionsMapper kNearestNeighboursOptionsMapper;

    @Test
    public void testMapKNearestNeighboursOptions() {
        KNearestNeighboursOptions kNearestNeighboursOptions = new KNearestNeighboursOptions();
        DistanceBuilder distanceBuilder = new DistanceBuilder();
        for (DistanceType distanceType : DistanceType.values()) {
            kNearestNeighboursOptions.setDistanceType(distanceType);
            assertThat(kNearestNeighboursOptionsMapper.map(kNearestNeighboursOptions).distance()).isInstanceOf(
                    distanceType.handle(distanceBuilder).getClass());
        }
        kNearestNeighboursOptions.setMaximumFractionDigits(4);
        kNearestNeighboursOptions.setNumNeighbours(25);
        kNearestNeighboursOptions.setWeight(0.5D);
        KNearestNeighbours kNearestNeighbours = kNearestNeighboursOptionsMapper.map(kNearestNeighboursOptions);
        assertThat(kNearestNeighbours.getDecimalFormat().getMaximumFractionDigits()).isEqualTo(
                kNearestNeighboursOptions.getMaximumFractionDigits());
        assertThat(kNearestNeighbours.getNumNeighbours()).isEqualTo(kNearestNeighboursOptions.getNumNeighbours());
        assertThat(kNearestNeighbours.getWeight()).isEqualTo(kNearestNeighboursOptions.getWeight());
    }
}
