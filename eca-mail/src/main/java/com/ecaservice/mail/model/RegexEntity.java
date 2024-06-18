package com.ecaservice.mail.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Regular expression persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "regex")
public class RegexEntity extends BaseEntity {

    /**
     * Regex code
     */
    @Column(name = "regex_code", unique = true, nullable = false)
    private String regexCode;

    /**
     * Regex string
     */
    @Column(nullable = false)
    private String regex;

    /**
     * Regex description
     */
    @Column(nullable = false)
    private String description;
}
