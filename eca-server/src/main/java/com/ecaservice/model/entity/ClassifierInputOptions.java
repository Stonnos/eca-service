package com.ecaservice.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Classifier input options entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "classifier_input_options")
public class ClassifierInputOptions {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Input option name
     */
    @Column(name = "option_name")
    private String optionName;

    /**
     * Input option value
     */
    @Column(name = "option_value")
    private String optionValue;

    /**
     * Input option order
     */
    @Column(name = "option_order")
    private Integer optionOrder;
}
