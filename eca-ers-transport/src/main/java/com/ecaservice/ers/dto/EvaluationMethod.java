package com.ecaservice.ers.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Evaluation method.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
@Getter
public enum EvaluationMethod {

    /**
     * Use training data
     */
    TRAINING_DATA("Использование обучающего множества"),

    /**
     * Use k * V - folds cross - validation method
     */
    CROSS_VALIDATION("V-блочная кросс-проверка");

    private final String description;
}
