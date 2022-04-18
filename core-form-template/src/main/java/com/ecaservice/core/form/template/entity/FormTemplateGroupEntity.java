package com.ecaservice.core.form.template.entity;

import lombok.Data;

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
     * Form templates list
     */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false)
    private List<FormTemplateEntity> templates;
}
