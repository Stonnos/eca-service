package com.ecaservice.data.loader.entity;

import com.ecaservice.data.loader.dto.AttributeInfo;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;

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
    @Column(name = "md5_hash", nullable = false, unique = true)
    private String md5Hash;

    /**
     * Instances object path
     */
    @Column(name = "object_path", nullable = false)
    private String objectPath;

    /**
     * Attributes list.
     */
    @Type(JsonType.class)
    @Column(name = "attributes", columnDefinition = "jsonb")
    private List<AttributeInfo> attributes;
}
