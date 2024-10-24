package com.ecaservice.server.model.entity;

import com.ecaservice.server.model.data.AttributeMetaInfo;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.util.List;

/**
 * Attributes info entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "attributes_info")
public class AttributesInfoEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Attributes list.
     */
    @Type(JsonType.class)
    @Column(name = "attributes", columnDefinition = "jsonb")
    private List<AttributeMetaInfo> attributes;

    /**
     * Training data info
     */
    @OneToOne
    @JoinColumn(name = "instances_info_id", nullable = false)
    private InstancesInfo instancesInfo;
}
