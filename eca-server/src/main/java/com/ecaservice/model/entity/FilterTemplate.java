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
import javax.persistence.OrderBy;
import javax.persistence.Table;
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
    @Column(name = "template_name")
    private String templateName;

    /**
     * Template type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "template_type")
    private FilterTemplateType templateType;

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
