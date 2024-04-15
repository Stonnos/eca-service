package com.ecaservice.auto.test.entity.autotest;

import com.ecaservice.base.model.ExperimentType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.TypeDef;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 * Experiment request persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "experiment_request")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ExperimentRequestEntity extends BaseEvaluationRequestEntity {

    /**
     * Experiment type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "experiment_type")
    private ExperimentType experimentType;
}
