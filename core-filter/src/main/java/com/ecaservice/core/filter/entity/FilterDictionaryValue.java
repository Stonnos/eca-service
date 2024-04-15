package com.ecaservice.core.filter.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Filter field value persistence model. Using for reference filter type
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "filter_dictionary_value")
public class FilterDictionaryValue {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Label string
     */
    @Column(nullable = false)
    private String label;

    /**
     * String value
     */
    @Column(nullable = false)
    private String value;
}
