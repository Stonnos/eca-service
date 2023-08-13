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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.List;

/**
 * Filter template persistence model.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "filter_template")
public class FilterTemplate {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Template name
     */
    @Column(name = "template_name", nullable = false)
    private String templateName;

    /**
     * Template type
     */
    @Column(name = "template_type", nullable = false, unique = true)
    private String templateType;

    /**
     * Filter fields list
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "filter_template_id", nullable = false)
    @OrderBy("fieldOrder")
    private List<FilterField> fields;
}
