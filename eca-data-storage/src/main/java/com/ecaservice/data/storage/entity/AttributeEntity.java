package com.ecaservice.data.storage.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.List;

/**
 * Attribute persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "attribute", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"instances_id", "_index"},
                name = "instances_id_index_unique_index"),
        @UniqueConstraint(columnNames = {"instances_id", "column_name"},
                name = "instances_id_column_name_unique_index"),
        @UniqueConstraint(columnNames = {"instances_id", "attribute_name"},
                name = "instances_id_attribute_name_unique_index"),
})
@NamedEntityGraph(name = "attributeValues",
        attributeNodes = {
                @NamedAttributeNode(value = "values"),
                @NamedAttributeNode(value = "instancesEntity")
        }
)
public class AttributeEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Attribute name
     */
    @Column(name = "attribute_name", nullable = false, updatable = false)
    private String attributeName;

    /**
     * Column name in database
     */
    @Column(name = "column_name", nullable = false, updatable = false)
    private String columnName;

    /**
     * Attribute type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "_type", nullable = false, updatable = false)
    private AttributeType type;

    /**
     * Is selected?
     */
    private boolean selected;

    /**
     * Attribute index
     */
    @Column(name = "_index", nullable = false, updatable = false)
    private int index;

    /**
     * Instances id
     */
    @ManyToOne
    @JoinColumn(name = "instances_id", nullable = false)
    private InstancesEntity instancesEntity;

    /**
     * Attribute values list for nominal attribute
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "attribute_id", nullable = false)
    @OrderBy("index")
    private List<AttributeValueEntity> values;
}
