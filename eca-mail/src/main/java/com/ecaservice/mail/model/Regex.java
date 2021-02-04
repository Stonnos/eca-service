package com.ecaservice.mail.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
public class Regex extends BaseEntity {

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
