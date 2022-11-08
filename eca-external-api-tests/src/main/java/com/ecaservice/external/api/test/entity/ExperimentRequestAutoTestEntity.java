package com.ecaservice.external.api.test.entity;

import com.ecaservice.test.common.model.MatchResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * Experiment request auto test persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "experiment_request_auto_test")
public class ExperimentRequestAutoTestEntity extends AutoTestEntity {

    /**
     * Expected models number in experiment history
     */
    @Column(name = "expected_pct_correct")
    private Integer expectedNumModels;

    /**
     * Actual models number in experiment history
     */
    @Column(name = "actual_pct_correct")
    private Integer actualNumModels;

    /**
     * Models number match result in experiment history
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "pct_correct_match_result")
    private MatchResult numModelsMatchResult;
}
