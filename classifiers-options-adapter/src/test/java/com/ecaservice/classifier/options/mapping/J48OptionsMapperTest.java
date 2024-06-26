package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.TestHelperUtils;
import com.ecaservice.classifier.options.model.J48Options;
import eca.trees.J48;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link J48OptionsMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(J48OptionsMapperImpl.class)
class J48OptionsMapperTest {

    @Autowired
    private J48OptionsMapper j48OptionsMapper;

    @Test
    void testMapToJ48() {
        J48Options j48Options = TestHelperUtils.createJ48Options();
        J48 j48 = j48OptionsMapper.map(j48Options);
        assertThat(j48.getMinNumObj()).isEqualTo(j48Options.getMinNumObj());
        assertThat(j48.getBinarySplits()).isTrue();
        assertThat(j48.getUnpruned()).isFalse();
        assertThat(j48.getNumFolds()).isEqualTo(j48Options.getNumFolds());
    }
}
