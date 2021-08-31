package com.ecaservice.external.api.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

import static com.ecaservice.external.api.util.FieldSize.PRECISION;
import static com.ecaservice.external.api.util.FieldSize.SCALE;

/**
 * Evaluation request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "evaluation_request")
public class EvaluationRequestEntity extends EcaRequestEntity {

    /**
     * Classifier options
     */
    @Column(name = "classifier_options_json", columnDefinition = "text")
    private String classifierOptionsJson;

    /**
     * Classifier model absolute path
     */
    @Column(name = "classifier_absolute_path")
    private String classifierAbsolutePath;

    /**
     * Test instances number
     */
    @Column(name = "num_test_instances")
    private Integer numTestInstances;

    /**
     * Correctly classified instances number
     */
    @Column(name = "num_correct")
    private Integer numCorrect;

    /**
     * Incorrectly classified instances number
     */
    @Column(name = "num_incorrect")
    private Integer numIncorrect;

    /**
     * Correctly classified percentage
     */
    @Column(name = "pct_correct", precision = PRECISION, scale = SCALE)
    private BigDecimal pctCorrect;

    /**
     * Incorrectly classified percentage
     */
    @Column(name = "pct_incorrect", precision = PRECISION, scale = SCALE)
    private BigDecimal pctIncorrect;

    /**
     * Mean absolute error
     */
    @Column(name = "mean_absolute_error", precision = PRECISION, scale = SCALE)
    private BigDecimal meanAbsoluteError;

    /**
     * Classifier model file deleted date
     */
    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;
}
