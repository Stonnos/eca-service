package com.ecaservice.data.loader.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Instances persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "instances")
public class InstancesEntity {

    @Id
    @GeneratedValue
    public Long id;

    /**
     * Instances uuid
     */
    @Column(nullable = false, unique = true)
    public String uuid;

    /**
     * Relation name
     */
    @Column(name = "relation_name", nullable = false)
    private String relationName;

    /**
     * Instances number
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
    @Column(nullable = false)
    private LocalDateTime created;

    /**
     * Instances md5 hash
     */
    @Column(name = "md5_hash", nullable = false)
    private String md5Hash;

    /**
     * Instances object path
     */
    @Column(name = "object_path", nullable = false)
    private String objectPath;
}
