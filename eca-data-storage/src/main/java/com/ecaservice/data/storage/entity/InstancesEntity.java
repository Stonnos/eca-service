package com.ecaservice.data.storage.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
    private Long id;

    /**
     * Table name in database
     */
    @Column(name = "table_name", unique = true, nullable = false)
    private String tableName;

    /**
     * Instances uuid
     */
    @Column(unique = true, nullable = false)
    private String uuid;

    /**
     * ID column name in instances table
     */
    @Column(name = "id_column_name", nullable = false)
    private String idColumnName;

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

    /**
     * Class attribute
     */
    @ManyToOne
    @JoinColumn(name = "class_attribute_id")
    private AttributeEntity classAttribute;
}
