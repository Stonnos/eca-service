package com.ecaservice.external.api.test.model;

import com.ecaservice.external.api.dto.EvaluationRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Evaluation test data model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EvaluationTestDataModel extends AbstractTestDataModel<EvaluationRequestDto> {
}
