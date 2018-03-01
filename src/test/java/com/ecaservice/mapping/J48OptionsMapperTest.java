package com.ecaservice.mapping;

import com.ecaservice.model.options.J48Options;
import eca.trees.J48;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link J48OptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@Import(J48OptionsMapperImpl.class)
public class J48OptionsMapperTest {

    @Inject
    private J48OptionsMapper j48OptionsMapper;

    @Test
    public void testMapToJ48() {
        J48Options j48Options = new J48Options();
        j48Options.setBinarySplits(true);
        j48Options.setMinNumObj(10);
        j48Options.setUnpruned(false);
        j48Options.setNumFolds(5);
        J48 j48 = j48OptionsMapper.map(j48Options);
        assertThat(j48.getMinNumObj()).isEqualTo(j48Options.getMinNumObj());
        assertThat(j48.getBinarySplits()).isTrue();
        assertThat(j48.getUnpruned()).isFalse();
        assertThat(j48.getNumFolds()).isEqualTo(j48Options.getNumFolds());
    }
}
