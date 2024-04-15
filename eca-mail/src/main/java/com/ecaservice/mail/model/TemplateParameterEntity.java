package com.ecaservice.mail.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Template parameter persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "template_parameter")
public class TemplateParameterEntity extends BaseEntity {

    /**
     * Parameter name
     */
    @Column(name = "parameter_name", unique = true, nullable = false)
    private String parameterName;

    /**
     * Parameter description
     */
    @Column(nullable = false)
    private String description;

    /**
     * Parameter regex (Optional)
     */
    @ManyToOne
    @JoinColumn(name = "regex_id")
    private RegexEntity regexEntity;
}
