package com.ecaservice.data.storage.entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Export instances object persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "export_instances_object")
public class ExportInstancesObjectEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Instances id
     */
    @ManyToOne
    @JoinColumn(name = "instances_id", nullable = false)
    private InstancesEntity instancesEntity;

    /**
     * External data uuid in central data storage
     */
    @Column(name = "external_data_uuid", nullable = false)
    private String externalDataUuid;

    /**
     * Instances md5 hash
     */
    @Column(name = "md5_hash", nullable = false)
    private String md5Hash;

    /**
     * Creation date
     */
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime created;

    /**
     * Instances updates counter
     */
    @Column(name = "updates_counter")
    private int updatesCounter;
}
