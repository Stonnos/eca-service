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

    /**
     * Instances name
     */
    @Column(name = "relation_name")
    private String relationName;

    /**
     * Instances size
     */
    @Column(name = "num_instances")
    private Integer numInstances;

    /**
     * Attributes number
     */
    @Column(name = "num_attributes")
    private Integer numAttributes;

    /**
     * Classes number
     */
    @Column(name = "num_classes")
    private Integer numClasses;

    /**
     * Class attribute name
     */
    @Column(name = "class_name")
    private String className;

}
