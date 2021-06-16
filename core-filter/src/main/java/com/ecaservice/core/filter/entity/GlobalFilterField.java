package com.ecaservice.core.filter.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
