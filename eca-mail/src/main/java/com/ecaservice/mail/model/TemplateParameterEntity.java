package com.ecaservice.mail.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
    private Regex regex;
}
