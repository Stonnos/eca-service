package com.ecaservice.core.filter.entity;

import lombok.Data;

import javax.persistence.CascadeType;
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
    @Column(nullable = false)
    private String name;

    /**
     * Values list for reference filter type
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "filter_dictionary_id", nullable = false)
    private List<FilterDictionaryValue> values;
}
