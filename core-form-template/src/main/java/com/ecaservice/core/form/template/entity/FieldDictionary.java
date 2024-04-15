package com.ecaservice.core.form.template.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

/**
 * Field dictionary persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "form_field_dictionary")
public class FieldDictionary {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Dictionary name
     */
    @Column(nullable = false)
    private String name;

    /**
     * Values list for reference field type
     */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "dictionary_id", nullable = false)
    private List<FieldDictionaryValue> values;
}
