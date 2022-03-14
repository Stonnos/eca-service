package com.ecaservice.auto.test.entity.autotest;

import com.ecaservice.auto.test.model.evaluation.EvaluationResultsDetailsMatch;
import com.ecaservice.base.model.ExperimentType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.util.List;

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
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class ExperimentRequestEntity extends BaseEvaluationRequestEntity {

    /**
     * Experiment type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "experiment_type")
    private ExperimentType experimentType;

    /**
     * Experiment NEW status email received?
     */
    @Column(name = "new_status_email_received")
    private boolean newStatusEmailReceived;

    /**
     * Experiment IN_PROGRESS status email received?
     */
    @Column(name = "in_progress_status_email_received")
    private boolean inProgressStatusEmailReceived;

    /**
     * Experiment FINISHED status email received?
     */
    @Column(name = "finished_status_email_received")
    private boolean finishedStatusEmailReceived;

    /**
     * Experiment ERROR status email received?
     */
    @Column(name = "error_status_email_received")
    private boolean errorStatusEmailReceived;

    /**
     * Experiment TIMEOUT status email received?
     */
    @Column(name = "timeout_status_email_received")
    private boolean timeoutStatusEmailReceived;

    /**
     * Experiment download url
     */
    @Column(name = "download_url")
    private String downloadUrl;

    /**
     * Experiment results details.
     */
    @Type(type = "jsonb")
    @Column(name = "experiment_results_details", columnDefinition = "jsonb")
    private List<EvaluationResultsDetailsMatch> experimentResultDetails;
}
