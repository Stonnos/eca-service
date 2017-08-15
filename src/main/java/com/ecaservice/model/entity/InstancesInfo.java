package com.ecaservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

/**
 * Input data options model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class InstancesInfo {

    private String relationName;

    private Integer numInstances;

    private Integer numAttributes;

    private Integer numClasses;

}
