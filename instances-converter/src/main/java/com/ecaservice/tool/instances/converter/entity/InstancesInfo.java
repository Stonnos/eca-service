package com.ecaservice.tool.instances.converter.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * Instances info persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "instances_info")
public class InstancesInfo {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Data structure bytes
     */
    @Lob
    private byte[] structure;

    /**
     * Data MD5 hash
     */
    @Column(name = "data_md5_hash")
    private String dataMd5Hash;
}
