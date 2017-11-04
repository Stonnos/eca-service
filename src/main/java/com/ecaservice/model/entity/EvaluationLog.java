package com.ecaservice.model.entity;

import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.model.evaluation.EvaluationStatus;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Evaluation log persistence entity.
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

    /**
     * Request remote ip address
     */
    @Column(name = "ip_address")
    private String ipAddress;

    /**
     * Request creation date
     */
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    /**
     * Evaluation start date
     */
    @Column(name = "start_date")
    private LocalDateTime startDate;

    /**
     * Evaluation end date
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;

    /**
     * Classifier name
     */
    @Column(name = "classifier_name")
    private String classifierName;

    /**
     * Evaluation status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_status")
    private EvaluationStatus evaluationStatus;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    /**
     * Evaluation method
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_method")
    private EvaluationMethod evaluationMethod;

    /**
     * Classifier input options map
     */
    @ElementCollection
    @CollectionTable(name = "input_options")
    @MapKeyColumn(name = "option_name")
    @Column(name = "option_value")
    private Map<String, String> inputOptionsMap;

    /**
     * Evaluation options map
     */
    @ElementCollection
    @CollectionTable(name = "evaluation_options")
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "evaluation_option_name")
    @Column(name = "evaluation_option_value")
    private Map<EvaluationOption, String> evaluationOptionsMap;

    /**
     * Training data info
     */
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "instances_info_id")
    private InstancesInfo instancesInfo;

}
