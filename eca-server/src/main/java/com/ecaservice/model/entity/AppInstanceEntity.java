package com.ecaservice.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Application instance persistence entity (used in cluster environment).
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "app_instance")
public class AppInstanceEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Unique instance name
     */
    @Column(name = "instance_name", nullable = false, unique = true)
    private String instanceName;
}
