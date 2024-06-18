package com.ecaservice.data.loader.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

/**
 * Instances persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "instances",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"client_id", "md5_hash"},
                name = "instances_client_id_md5_hash_unique_index")
})
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
     * Object expiration date
     */
    @Column(nullable = false)
    private LocalDateTime expireAt;

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
     * Username (client id)
     */
    @Column(name = "client_id")
    private String clientId;

    /**
     * Instances object path
     */
    @Column(name = "object_path", nullable = false)
    private String objectPath;
}
