package com.ecaservice.ers.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * Instances info persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "instances_info", indexes = @Index(columnList = "data_md5_hash", name = "data_md5_hash_index"))
public class InstancesInfo {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Data MD5 hash
     */
    @Column(name = "data_md5_hash", nullable = false)
    private String dataMd5Hash;

    /**
     * Instances name
     */
    @Column(name = "relation_name", nullable = false)
    private String relationName;

    /**
     * Instances size
     */
    @Column(name = "num_instances", nullable = false)
    private Integer numInstances;

    /**
     * Attributes number
     */
    @Column(name = "num_attributes", nullable = false)
    private Integer numAttributes;

    /**
     * Classes number
     */
    @Column(name = "num_classes", nullable = false)
    private Integer numClasses;

    /**
     * Class attribute name
     */
    @Column(name = "class_name", nullable = false)
    private String className;
}
