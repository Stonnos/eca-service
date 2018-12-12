package com.ecaservice.model.entity;

import com.ecaservice.web.dto.model.FilterType;
import com.ecaservice.web.dto.model.MatchMode;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

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
    private String name;

    /**
     * Field description
     */
    private String description;

    /**
     * Field order
     */
    @Column(name = "field_order", nullable = false)
    private int fieldOrder;

    /**
     * Filter type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "filter_type")
    private FilterType filterType;

    /**
     * Filter match mode
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "match_mode")
    private MatchMode matchMode;

    /**
     * Filter dictionary
     */
    @ManyToOne
    @JoinColumn(name = "filter_dictionary_id")
    private FilterDictionary dictionary;
}
