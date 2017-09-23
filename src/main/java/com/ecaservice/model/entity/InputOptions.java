package com.ecaservice.model.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Classifier input options model.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "input_options")
public class InputOptions {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "option_name", nullable = false)
    private String name;

    @Column(name = "option_value", nullable = false)
    private String value;

}
