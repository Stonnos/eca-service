package com.ecaservice.auto.test.entity.autotest;

import eca.core.DescriptiveEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Test feature enum.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum TestFeature implements DescriptiveEnum {

    /**
     * Checks evaluation results
     */
    EVALUATION_RESULTS("Контроль расчета основных показателей точности модели") {
        @Override
        public <T> T visit(TestFeatureVisitor<T> visitor) {
            return visitor.visitEvaluationResults();
        }
    },

    /**
     * Checks experiment email notifications
     */
    EXPERIMENT_EMAILS("Проверка получения email сообщений по эксперименту с контролем основных параметров") {
        @Override
        public <T> T visit(TestFeatureVisitor<T> visitor) {
            return visitor.visitExperimentEmailsFeature();
        }
    };

    /**
     * Description
     */
    private final String description;

    /**
     * Calls visitor.
     *
     * @param visitor - visitor interface
     * @param <T>     - result generic type
     * @return result
     */
    public abstract <T> T visit(TestFeatureVisitor<T> visitor);
}
