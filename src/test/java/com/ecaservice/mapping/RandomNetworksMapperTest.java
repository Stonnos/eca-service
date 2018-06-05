package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.options.RandomNetworkOptions;
import eca.ensemble.RandomNetworks;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link RandomNetworksMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RandomNetworksMapperTest {

    @Inject
    private RandomNetworksMapper randomNetworksMapper;

    @Test
    public void testMapRandomNetworks() {
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
