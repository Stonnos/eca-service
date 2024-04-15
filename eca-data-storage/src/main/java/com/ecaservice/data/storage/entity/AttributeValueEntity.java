package com.ecaservice.data.storage.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Attribute value persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "attribute_value", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"attribute_id", "_value"},
                name = "attribute_id_value_unique_index"),
        @UniqueConstraint(columnNames = {"attribute_id", "_index"},
                name = "attribute_id_index_unique_index")
})
public class AttributeValueEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Attribute value
     */
    @Column(name = "_value", nullable = false, updatable = false)
    private String value;

    /**
     * Value index
     */
    @Column(name = "_index", nullable = false, updatable = false)
    private int index;
}
