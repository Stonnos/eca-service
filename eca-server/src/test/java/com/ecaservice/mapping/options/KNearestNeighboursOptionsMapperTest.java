package com.ecaservice.mapping.options;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.model.options.KNearestNeighboursOptions;
import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.DistanceBuilder;
import eca.metrics.distances.DistanceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for checking {@link KNearestNeighboursOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({KNearestNeighboursOptionsMapperImpl.class, ExperimentConfig.class})
class KNearestNeighboursOptionsMapperTest {

    @Inject
    private KNearestNeighboursOptionsMapper kNearestNeighboursOptionsMapper;
    @Inject
    private ExperimentConfig experimentConfig;

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
        assertThat(kNearestNeighbours.getDecimalFormat().getMaximumFractionDigits()).isEqualTo(
                experimentConfig.getMaximumFractionDigits());
        assertThat(kNearestNeighbours.getNumNeighbours()).isEqualTo(kNearestNeighboursOptions.getNumNeighbours());
        assertThat(kNearestNeighbours.getWeight()).isEqualTo(kNearestNeighboursOptions.getWeight());
    }
}
