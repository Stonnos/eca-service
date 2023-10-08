package com.ecaservice.server.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

import static com.ecaservice.server.service.classifiers.ClassifierFormGroupTemplates.CLASSIFIERS_GROUP;
import static com.ecaservice.server.service.classifiers.ClassifierFormGroupTemplates.ENSEMBLE_CLASSIFIERS_GROUP;

/**
 * Classifier group templates type.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum ClassifierGroupTemplatesType {

    /**
     * Individual classifiers templates
     */
    INDIVIDUAL(Collections.singletonList(CLASSIFIERS_GROUP)),

    /**
     * Ensemble classifiers templates
     */
    ENSEMBLE(Collections.singletonList(ENSEMBLE_CLASSIFIERS_GROUP)),

    /**
     * All classifiers templates (individual and ensemble)
     */
    ALL(List.of(CLASSIFIERS_GROUP, ENSEMBLE_CLASSIFIERS_GROUP));

    /**
     * Template names
     */
    private final List<String> templateNames;
}
