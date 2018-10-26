package com.ecaservice.mapping.options;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.options.RandomNetworkOptions;
import eca.ensemble.RandomNetworks;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link RandomNetworkOptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(RandomNetworkOptionsMapperImpl.class)
public class RandomNetworksOptionsMapperTest {

    @Inject
    private RandomNetworkOptionsMapper randomNetworkOptionsMapper;

    @Test
    public void testMapRandomNetworksOptions() {
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
