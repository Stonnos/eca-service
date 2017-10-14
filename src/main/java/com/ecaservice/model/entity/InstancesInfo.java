package com.ecaservice.model.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Input data information model.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "instances_info")
public class InstancesInfo {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "relation_name", nullable = false)
    private String relationName;

    @Column(name = "num_instances", nullable = false)
    private Integer numInstances;

    @Column(name = "num_attributes", nullable = false)
    private Integer numAttributes;

    @Column(name = "num_classes", nullable = false)
    private Integer numClasses;

    @OneToOne
    @JoinColumn(name = "evaluation_log_id")
    private EvaluationLog evaluationLog;

}
