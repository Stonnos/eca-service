package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.TestHelperUtils;
import com.ecaservice.classifier.options.model.J48Options;
import eca.trees.J48;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit tests for checking {@link J48Mapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(J48MapperImpl.class)
class J48MapperTest {

    @Autowired
    private J48Mapper j48Mapper;

    @Test
    void testMapJ48() {
        J48 j48 = TestHelperUtils.createJ48();
        J48Options j48Options = j48Mapper.map(j48);
        Assertions.assertThat(j48Options.getBinarySplits()).isEqualTo(j48.getBinarySplits());
        Assertions.assertThat(j48Options.getMinNumObj()).isEqualTo(j48.getMinNumObj());
        Assertions.assertThat(j48Options.getUnpruned()).isEqualTo(j48.getUnpruned());
        Assertions.assertThat(j48Options.getNumFolds()).isEqualTo(j48.getNumFolds());
    }
}
