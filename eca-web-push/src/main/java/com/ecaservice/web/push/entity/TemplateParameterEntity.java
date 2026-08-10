package com.ecaservice.web.push.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
