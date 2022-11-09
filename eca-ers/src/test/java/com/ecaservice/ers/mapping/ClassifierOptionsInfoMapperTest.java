package com.ecaservice.ers.mapping;

import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.ers.model.ClassifierOptionsInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;

import static com.ecaservice.ers.TestHelperUtils.buildClassifierOptionsInfo;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link ClassifierOptionsInfoMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(ClassifierOptionsInfoMapperImpl.class)
class ClassifierOptionsInfoMapperTest {

    private static final Map<String, String> INPUT_OPTIONS_MAP = Collections.singletonMap("D", "22");

    @Inject
    private ClassifierOptionsInfoMapper classifierOptionsInfoMapper;

    @Test
    void testMapClassifierOptionsInfo() {
        ClassifierOptionsInfo classifierOptionsInfo =
                buildClassifierOptionsInfo(INPUT_OPTIONS_MAP, Collections.emptyList());
        ClassifierReport classifierReport = classifierOptionsInfoMapper.map(classifierOptionsInfo);
        assertThat(classifierReport.getClassifierName()).isEqualTo(
                classifierOptionsInfo.getClassifierName());
        assertThat(classifierReport.getClassifierDescription()).isEqualTo(
                classifierOptionsInfo.getClassifierDescription());
    }
}
