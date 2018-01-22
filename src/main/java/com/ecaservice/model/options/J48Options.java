package com.ecaservice.model.options;

import lombok.Data;

/**
 * J48 algorithm input options model.
 *
 * @author Roman Batygin
 */
@Data
public class J48Options extends ClassifierOptions {

    private Integer minNumObj;

    private Boolean binarySplits;

    private Boolean unpruned;

    private Integer numFolds;
}
