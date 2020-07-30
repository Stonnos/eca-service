package com.ecaservice.data.storage.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

import static com.ecaservice.data.storage.config.Constants.INSTANCES_TABLE_NAME;

/**
 * Instances persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = INSTANCES_TABLE_NAME)
public class InstancesEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Table name in database
     */
    @Column(name = "table_name", unique = true, nullable = false)
    private String tableName;

    /**
     * Instances number
     */
    @Column(name = "num_instances")
    private Integer numInstances;

    /**
     * Attributes number
     */
    @Column(name = "num_attributes")
    private Integer numAttributes;

    /**
     * Creation date
     */
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime created;

    /**
     * User name
     */
    @Column(name = "created_by", nullable = false)
    private String createdBy;
}
