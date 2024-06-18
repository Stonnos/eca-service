package com.ecaservice.core.filter.entity;

import com.ecaservice.web.dto.model.FilterFieldType;
import com.ecaservice.web.dto.model.MatchMode;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Filter field persistence model.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "filter_field")
public class FilterField {

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
     * Filter field type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "filter_field_type", nullable = false)
    private FilterFieldType filterFieldType;

    /**
     * Filter match mode
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "match_mode", nullable = false)
    private MatchMode matchMode;

    /**
     * Allow multiple values
     */
    private boolean multiple;

    /**
     * Filter dictionary
     */
    @ManyToOne
    @JoinColumn(name = "filter_dictionary_id")
    private FilterDictionary dictionary;
}
