package com.ecaservice.server.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Classifier template field persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "classifier_field")
public class ClassifierFieldEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Field name
     */
    @Column(name = "field_name", nullable = false)
    private String fieldName;

    /**
     * Field description
     */
    @Column(nullable = false)
    private String description;

    /**
     * Field order
     */
    @Column(name = "field_order", nullable = false)
    private int fieldOrder;

    /**
     * Field type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false)
    private FieldType fieldType;

    /**
     * Min. value
     */
    @Column(name = "min_value")
    private BigDecimal minValue;

    /**
     * Max. value
     */
    @Column(name = "max_value")
    private BigDecimal maxValue;

    /**
     * Max length value
     */
    @Column(name = "max_length")
    private Integer maxLength;

    /**
     * Pattern value (regular expression)
     */
    private String pattern;

    /**
     * Field dictionary values
     */
    @ManyToOne
    @JoinColumn(name = "dictionary_id")
    private FieldDictionary dictionary;

    /**
     * Default value
     */
    @Column(name = "default_value")
    private String defaultValue;
}
