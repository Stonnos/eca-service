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
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
     * Created date
     */
    private LocalDateTime created;

    /**
     * Filter fields list
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "filter_template_id", nullable = false)
    @OrderBy("fieldOrder")
    private List<FilterField> fields;
}
