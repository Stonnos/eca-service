package com.ecaservice.data.storage.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
        @UniqueConstraint(columnNames = {"attribute_id", "value_order"},
                name = "attribute_id_value_order_unique_index")
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
     * Value order
     */
    @Column(name = "value_order", nullable = false, updatable = false)
    private int valueOrder;
}
