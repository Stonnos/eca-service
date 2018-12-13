package com.ecaservice.model.experiment;

import com.ecaservice.model.dictionary.ExperimentTypeDictionary;

/**
 * Experiment type.
 *
 * @author Roman Batygin
 */
public enum ExperimentType {

    /**
     * Optimal options automatic selection for neural networks.
     */
    NEURAL_NETWORKS(ExperimentTypeDictionary.NEURAL_NETWORKS_NAME) {
        @Override
        public <T, P> T internalHandle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseNeuralNetwork(parameter);
        }
    },

    /**
     * Optimal options automatic selection for heterogeneous ensemble algorithm.
     */
    HETEROGENEOUS_ENSEMBLE(ExperimentTypeDictionary.HETEROGENEOUS_ENSEMBLE_NAME) {
        @Override
        public <T, P> T internalHandle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseHeterogeneousEnsemble(parameter);
        }
    },

    /**
     * Optimal options automatic selection for modified heterogeneous ensemble algorithm.
     */
    MODIFIED_HETEROGENEOUS_ENSEMBLE(ExperimentTypeDictionary.MODIFIED_HETEROGENEOUS_ENSEMBLE_NAME) {
        @Override
        public <T, P> T internalHandle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseModifiedHeterogeneousEnsemble(parameter);
        }
    },

    /**
     * Optimal options automatic selection for AdaBoost algorithm.
     */
    ADA_BOOST(ExperimentTypeDictionary.ADA_BOOST_NAME) {
        @Override
        public <T, P> T internalHandle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseAdaBoost(parameter);
        }
    },

    /**
     * Optimal options automatic selection for stacking algorithm.
     */
    STACKING(ExperimentTypeDictionary.STACKING_NAME) {
        @Override
        public <T, P> T internalHandle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseStacking(parameter);
        }
    },

    /**
     * Optimal options automatic selection for k - nearest neighbours algorithm.
     */
    KNN(ExperimentTypeDictionary.KNN_NAME) {
        @Override
        public <T, P> T internalHandle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseKNearestNeighbours(parameter);
        }
    },

    /**
     * Optimal options automatic selection Random forests algorithm.
     */
    RANDOM_FORESTS(ExperimentTypeDictionary.RANDOM_FORESTS_NAME) {
        @Override
        public <T, P> T internalHandle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseRandomForests(parameter);
        }
    },

    /**
     * Optimal options automatic selection for stacking algorithm using cross - validation method for
     * creation meta data set.
     */
    STACKING_CV(ExperimentTypeDictionary.STACKING_CV_NAME) {
        @Override
        public <T, P> T internalHandle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseStackingCV(parameter);
        }
    },

    /**
     * Optimal options automatic selection decision tree algorithms.
     */
    DECISION_TREE(ExperimentTypeDictionary.DECISION_TREE_NAME) {
        @Override
        public <T, P> T internalHandle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseDecisionTree(parameter);
        }
    };

    private String description;

    ExperimentType(String description) {
        this.description = description;
    }

    /**
     * Returns experiment description.
     *
     * @return experiment description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Finds experiment type by description
     *
     * @param description description string.
     * @return {@link ExperimentType} object
     */
    public static ExperimentType findByDescription(String description) {
        for (ExperimentType experimentType : values()) {
            if (experimentType.getDescription().equals(description)) {
                return experimentType;
            }
        }
        return null;
    }

    /**
     * Visitor pattern common method.
     *
     * @param visitor   {@link ExperimentTypeVisitor} object
     * @param parameter param
     * @param <T>       - result generic type
     * @param <P>       - param generic type
     * @return result generic class
     */
    public <T, P> T handle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
        T result = internalHandle(visitor, parameter);
        visitor.afterHandle(result, parameter);
        return result;
    }

    /**
     * Visitor pattern internal handling method.
     *
     * @param visitor   {@link ExperimentTypeVisitor} object
     * @param parameter param
     * @param <T>       - result generic type
     * @param <P>       - param generic type
     * @return result generic class
     */
    public abstract <T, P> T internalHandle(ExperimentTypeVisitor<T, P> visitor, P parameter);
}
