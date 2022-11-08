package com.ecaservice.external.api.test.model;

import com.ecaservice.external.api.dto.ExperimentRequestDto;
import com.ecaservice.external.api.dto.ExperimentResultsResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Experiment test data model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExperimentTestDataModel extends AbstractTestDataModel<ExperimentRequestDto, ExperimentResultsResponseDto> {
}
