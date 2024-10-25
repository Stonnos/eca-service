package com.ecaservice.server.model.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

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
     * Instances uuid from central data storage
     */
    @Column(nullable = false, unique = true)
    private String uuid;

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

    /**
     * Json instances MD5 hash
     */
    @Column(name = "data_md5_hash", nullable = false, unique = true)
    private String dataMd5Hash;

    /**
     * Created date
     */
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

}
