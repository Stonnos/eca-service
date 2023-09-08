package com.ecaservice.data.storage.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
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
     * Instances uuid
     */
    @Column(name = "instances_uuid", nullable = false)
    private String instancesUuid;

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
     * Object expiration date
     */
    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

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
