package com.ecaservice.model.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
    @Enumerated(EnumType.STRING)
    @Column(name = "template_type", nullable = false)
    private FilterTemplateType templateType;

    /**
     * Filter fields list
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "global_filter_template_id", nullable = false)
    private List<GlobalFilterField> fields;
}
