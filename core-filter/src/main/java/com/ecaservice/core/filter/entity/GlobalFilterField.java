package com.ecaservice.core.filter.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Global filter field persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "global_filter_field")
public class GlobalFilterField {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Field name
     */
    @Column(name = "field_name", nullable = false)
    private String fieldName;
}
