package com.ecaservice.core.form.template.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Field dictionary value persistence entity. Using for reference field type
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "form_field_dictionary_value")
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
