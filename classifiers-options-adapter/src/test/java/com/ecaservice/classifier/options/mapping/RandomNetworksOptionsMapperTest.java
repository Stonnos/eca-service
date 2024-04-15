package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.TestHelperUtils;
import com.ecaservice.classifier.options.model.RandomNetworkOptions;
import eca.ensemble.RandomNetworks;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit tests for checking {@link RandomNetworkOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(RandomNetworkOptionsMapperImpl.class)
class RandomNetworksOptionsMapperTest {

    @Autowired
    private RandomNetworkOptionsMapper randomNetworkOptionsMapper;

    @Test
    void testMapRandomNetworksOptions() {
        RandomNetworkOptions options = TestHelperUtils.createRandomNetworkOptions();
        RandomNetworks randomNetworks = randomNetworkOptionsMapper.map(options);
        Assertions.assertThat(randomNetworks.getSeed()).isEqualTo(options.getSeed());
        Assertions.assertThat(randomNetworks.getNumIterations()).isEqualTo(options.getNumIterations());
        Assertions.assertThat(randomNetworks.getNumThreads()).isEqualTo(options.getNumThreads());
        Assertions.assertThat(randomNetworks.getMinError()).isEqualTo(options.getMinError());
        Assertions.assertThat(randomNetworks.getMaxError()).isEqualTo(options.getMaxError());
        Assertions.assertThat(randomNetworks.isUseBootstrapSamples()).isEqualTo(options.getUseBootstrapSamples());
    }
}
