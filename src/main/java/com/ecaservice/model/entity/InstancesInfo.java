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

    @Column(name = "relation_name")
    private String relationName;

    @Column(name = "num_instances")
    private Integer numInstances;

    @Column(name = "num_attributes")
    private Integer numAttributes;

    @Column(name = "num_classes")
    private Integer numClasses;

    @Column(name = "class_name")
    private String className;

}
