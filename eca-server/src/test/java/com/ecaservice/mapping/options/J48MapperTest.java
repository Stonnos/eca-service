package com.ecaservice.mapping.options;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.options.J48Options;
import eca.trees.J48;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link J48Mapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(J48MapperImpl.class)
public class J48MapperTest {

    @Inject
    private J48Mapper j48Mapper;

    @Test
    public void testMapJ48() {
        J48 j48 = TestHelperUtils.createJ48();
        J48Options j48Options = j48Mapper.map(j48);
        Assertions.assertThat(j48Options.getBinarySplits()).isEqualTo(j48.getBinarySplits());
        Assertions.assertThat(j48Options.getMinNumObj()).isEqualTo(j48.getMinNumObj());
        Assertions.assertThat(j48Options.getUnpruned()).isEqualTo(j48.getUnpruned());
        Assertions.assertThat(j48Options.getNumFolds()).isEqualTo(j48.getNumFolds());
    }
}
