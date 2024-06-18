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
import jakarta.persistence.Table;
import java.util.List;

/**
 * Form template group persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "form_template_group")
public class FormTemplateGroupEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Group name
     */
    @Column(name = "group_name", nullable = false, unique = true)
    private String groupName;

    /**
     * Group title
     */
    @Column(name = "group_title")
    private String groupTitle;

    /**
     * Form templates list
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false)
    private List<FormTemplateEntity> templates;
}
