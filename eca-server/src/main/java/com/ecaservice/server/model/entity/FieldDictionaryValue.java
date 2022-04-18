package com.ecaservice.server.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Field value persistence entity. Using for reference field type
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "field_dictionary_value")
public class FieldDictionaryValue {

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
