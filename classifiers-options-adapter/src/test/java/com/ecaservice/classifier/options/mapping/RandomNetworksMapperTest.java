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
 * Unit tests for checking {@link RandomNetworksMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(RandomNetworksMapperImpl.class)
class RandomNetworksMapperTest {

    @Autowired
    private RandomNetworksMapper randomNetworksMapper;

    @Test
    void testMapRandomNetworks() {
        RandomNetworks randomNetworks = TestHelperUtils.createRandomNetworks();
        RandomNetworkOptions randomNetworkOptions = randomNetworksMapper.map(randomNetworks);
        Assertions.assertThat(randomNetworkOptions.getUseBootstrapSamples()).isEqualTo(
                randomNetworkOptions.getUseBootstrapSamples());
        Assertions.assertThat(randomNetworkOptions.getSeed()).isEqualTo(randomNetworkOptions.getSeed());
        Assertions.assertThat(randomNetworkOptions.getNumThreads()).isEqualTo(randomNetworkOptions.getNumThreads());
        Assertions.assertThat(randomNetworkOptions.getNumIterations()).isEqualTo(
                randomNetworkOptions.getNumIterations());
        Assertions.assertThat(randomNetworkOptions.getMinError()).isEqualTo(randomNetworkOptions.getMinError());
        Assertions.assertThat(randomNetworkOptions.getMaxError()).isEqualTo(randomNetworkOptions.getMaxError());
    }
}
