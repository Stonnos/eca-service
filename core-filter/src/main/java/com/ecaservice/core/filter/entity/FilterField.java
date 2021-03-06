package com.ecaservice.core.filter.entity;

import com.ecaservice.web.dto.model.FilterFieldType;
import com.ecaservice.web.dto.model.MatchMode;
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
