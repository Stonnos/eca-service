package com.ecaservice.core.form.template.entity;

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
     * Object class
     */
    @Column(name = "object_class")
    private String objectClass;

    /**
     * Object type
     */
    @Column(name = "object_type")
    private String objectType;

    /**
     * Template title
     */
    @Column(name = "template_title", nullable = false)
    private String templateTitle;

    /**
     * Template title field reference (used to get title from specified field value)
     */
    @Column(name = "template_title_field_ref")
    private String templateTitleFieldRef;

    /**
     * Fields list
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "template_id", nullable = false)
    @OrderBy("fieldOrder")
    private List<FormFieldEntity> fields;
}
