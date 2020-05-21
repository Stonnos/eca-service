package com.ecaservice.mapping.options;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.options.RandomNetworkOptions;
import eca.ensemble.RandomNetworks;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link RandomNetworksMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(RandomNetworksMapperImpl.class)
public class RandomNetworksMapperTest {

    @Inject
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
