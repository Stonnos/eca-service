package com.ecaservice.core.filter.entity;

import lombok.Data;

import jakarta.persistence.CascadeType;
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
 * Filter dictionary persistence model.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "filter_dictionary")
public class FilterDictionary {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Dictionary name
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Values list for reference filter type
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "filter_dictionary_id", nullable = false)
    private List<FilterDictionaryValue> values;
}
