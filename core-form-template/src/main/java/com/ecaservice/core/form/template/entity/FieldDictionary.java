package com.ecaservice.core.form.template.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Field dictionary persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "field_dictionary")
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
