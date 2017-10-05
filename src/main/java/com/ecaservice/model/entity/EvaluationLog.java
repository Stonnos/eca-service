package com.ecaservice.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    @Column(name = "operation_date", nullable = false)
    private LocalDateTime operationDate;

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

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "numFolds", column =
            @Column(name = "number_of_folds")),
            @AttributeOverride(name = "numTests", column =
            @Column(name = "number_of_tests")),
    })
    private EvaluationOptions evaluationOptions;

    @OneToOne(mappedBy = "evaluationLog", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private InstancesInfo instancesInfo;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "evaluation_log_id")
    private List<InputOptions> inputOptionsList;

}
