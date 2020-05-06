package com.ecaservice.mapping;

import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.model.entity.ErsResponseStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link ErsResponseStatusMapper} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(ErsResponseStatusMapperImpl.class)
public class ErsResponseStatusMapperTest {

    @Inject
    private ErsResponseStatusMapper ersResponseStatusMapper;

    @Test
    public void testMapSuccessStatus() {
        Assertions.assertThat(ersResponseStatusMapper.map(ResponseStatus.SUCCESS)).isEqualTo(ErsResponseStatus.SUCCESS);
    }

    @Test
    public void testMapDuplicateRequestIdStatus() {
        Assertions.assertThat(ersResponseStatusMapper.map(ResponseStatus.DUPLICATE_REQUEST_ID)).isEqualTo(
                ErsResponseStatus.DUPLICATE_REQUEST_ID);
    }

    @Test
    public void testMapDataNotFoundStatus() {
        Assertions.assertThat(ersResponseStatusMapper.map(ResponseStatus.DATA_NOT_FOUND)).isEqualTo(
                ErsResponseStatus.DATA_NOT_FOUND);
    }

    @Test
    public void testMapResultsNotFoundStatus() {
        Assertions.assertThat(ersResponseStatusMapper.map(ResponseStatus.RESULTS_NOT_FOUND)).isEqualTo(
                ErsResponseStatus.RESULTS_NOT_FOUND);
    }
}
