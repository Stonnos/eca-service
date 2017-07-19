package com.ecaservice.model.entity;

import com.ecaservice.model.EvaluationMethod;
import lombok.Data;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

/**
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "evaluation_log")
public class EvaluationLog {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "classifier_name", nullable = false)
    private String classifierName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "evaluation_date", nullable = false)
    private Date evaluationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_status", nullable = false)
    private EvaluationStatus evaluationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_method", nullable = false)
    private EvaluationMethod evaluationMethod;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "relationName", column =
                @Column(name = "relation_name", nullable = false)),
            @AttributeOverride(name = "numInstances", column =
                @Column(name = "number_of_instances", nullable = false)),
            @AttributeOverride(name = "numAttributes", column =
                @Column(name = "number_of_attributes", nullable = false)),
            @AttributeOverride(name = "numClasses", column =
                @Column(name = "number_of_classes", nullable = false))
    })
    private InstancesInfo instancesInfo;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "evaluation_log_id")
    private List<InputOptions> inputOptionsList;

}
