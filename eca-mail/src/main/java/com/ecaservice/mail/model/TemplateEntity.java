package com.ecaservice.mail.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Notification template persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "template")
public class TemplateEntity extends BaseEntity {

    /**
     * Template code
     */
    @Column(name = "template_code", unique = true, nullable = false)
    private String code;

    /**
     * Template description
     */
    @Column(nullable = false)
    private String description;

    /**
     * Template subject
     */
    @Column(name = "template_subject", nullable = false)
    private String subject;

    /**
     * Template body
     */
    @Column(name = "template_body", columnDefinition = "text")
    private String body;

    /**
     * Template parameters
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "templates_parameters", joinColumns = @JoinColumn(name = "template_id"),
            inverseJoinColumns = @JoinColumn(name = "param_id"))
    private List<TemplateParameterEntity> parameters;
}
