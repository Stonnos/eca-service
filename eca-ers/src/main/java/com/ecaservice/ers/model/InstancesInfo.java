package com.ecaservice.ers.model;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

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
    @Column(name = "data_md5_hash", nullable = false, unique = true)
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


    /**
     * Created date
     */
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
}
