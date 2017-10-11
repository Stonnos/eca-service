package com.ecaservice.model.entity;

import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.model.evaluation.EvaluationStatus;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Evaluation log model.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "evaluation_log")
public class EvaluationLog {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "classifier_name", nullable = false)
    private String classifierName;

    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_status", nullable = false)
    private EvaluationStatus evaluationStatus;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_method", nullable = false)
    private EvaluationMethod evaluationMethod;

    @ElementCollection
    @CollectionTable(name = "input_options")
    @MapKeyColumn(name = "option_name")
    @Column(name = "option_value", nullable = false)
    private Map<String, String> inputOptionsMap;

    @ElementCollection
    @CollectionTable(name = "evaluation_options")
    @MapKeyColumn(name = "option_name")
    @Column(name = "option_value")
    private Map<EvaluationOption, String> evaluationOptionsMap;

    @OneToOne(mappedBy = "evaluationLog", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private InstancesInfo instancesInfo;

}
