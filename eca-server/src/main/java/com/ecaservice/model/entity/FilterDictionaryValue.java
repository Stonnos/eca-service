package com.ecaservice.model.entity;

import lombok.Data;

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
    private String label;

    /**
     * String value
     */
    private String value;
}
