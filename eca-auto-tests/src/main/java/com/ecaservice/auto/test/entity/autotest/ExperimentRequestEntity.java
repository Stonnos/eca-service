package com.ecaservice.auto.test.entity.autotest;

import com.ecaservice.base.model.ExperimentType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import static com.ecaservice.auto.test.util.Constraints.DOWNLOAD_URL_MAX_LENGTH;

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
