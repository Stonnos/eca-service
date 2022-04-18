package com.ecaservice.server.model.entity;

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
import java.time.LocalDateTime;
import java.util.List;

/**
 * Classifier template persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "classifier_template")
public class ClassifierTemplateEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Template creation date
     */
    @Column(nullable = false)
    private LocalDateTime created;

    /**
     * Template name
     */
    @Column(name = "template_name", nullable = false)
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
    private List<ClassifierFieldEntity> fields;
}
