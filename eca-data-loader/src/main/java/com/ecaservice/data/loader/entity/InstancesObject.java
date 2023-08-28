package com.ecaservice.data.loader.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Instances object persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "instances_object")
public class InstancesObject {

    @Id
    @GeneratedValue
    private Long id;

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

    /**
     * Created date
     */
    @Column(nullable = false)
    private LocalDateTime created;
}
