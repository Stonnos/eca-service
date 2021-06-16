package com.ecaservice.core.filter.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
