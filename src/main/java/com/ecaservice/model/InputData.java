package com.ecaservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

/**
 * Input data model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InputData {

    private AbstractClassifier classifier;

    private Instances data;
}
