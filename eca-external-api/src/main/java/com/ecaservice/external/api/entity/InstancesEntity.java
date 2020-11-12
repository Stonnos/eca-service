package com.ecaservice.external.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
    private Long id;

    /**
     * Instances uuid
     */
    @Column(name = "uuid", nullable = false, unique = true)
    private String uuid;

    /**
     * Instances absolute path on file system
     */
    @Column(name = "absolute_path", nullable = false)
    private String absolutePath;
}
