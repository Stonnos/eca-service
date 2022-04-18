package com.ecaservice.core.form.template.entity;

import lombok.Data;

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
 * Form template persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "form_template")
public class FormTemplateEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Template name
     */
    @Column(name = "template_name", nullable = false, unique = true)
    private String templateName;

    /**
     * Template title
     */
    @Column(name = "template_title", nullable = false)
    private String templateTitle;

    /**
     * Fields list
     */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "template_id", nullable = false)
    @OrderBy("fieldOrder")
    private List<FormFieldEntity> fields;
}
