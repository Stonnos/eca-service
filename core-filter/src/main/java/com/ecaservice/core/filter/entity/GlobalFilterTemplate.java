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
 * Global filter persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "global_filter_template")
public class GlobalFilterTemplate {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "filter_name", nullable = false)
    private String filterName;

    /**
     * Template type
     */
    @Column(name = "template_type", nullable = false, unique = true)
    private String templateType;

    /**
     * Filter fields list
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "global_filter_template_id", nullable = false)
    private List<GlobalFilterField> fields;
}
