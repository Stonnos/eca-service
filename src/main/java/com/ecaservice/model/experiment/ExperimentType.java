package com.ecaservice.model.experiment;

/**
 * Experiment type.
 *
 * @author Roman Batygin
 */
public enum ExperimentType {

    NEURAL_NETWORKS {
        @Override
        public <T, P> T internalHandle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseNeuralNetwork(parameter);
        }
    },

    HETEROGENEOUS_ENSEMBLE {
        @Override
        public <T, P> T internalHandle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseHeterogeneousEnsemble(parameter);
        }
    },

    MODIFIED_HETEROGENEOUS_ENSEMBLE {
        @Override
        public <T, P> T internalHandle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseModifiedHeterogeneousEnsemble(parameter);
        }
    },

    ADA_BOOST {
        @Override
        public <T, P> T internalHandle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseAdaBoost(parameter);
        }
    },

    STACKING {
        @Override
        public <T, P> T internalHandle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseStacking(parameter);
        }
    },

    KNN {
        @Override
        public <T, P> T internalHandle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseKNearestNeighbours(parameter);
        }
    };

    /**
     * Visitor pattern common method.
     * @param visitor {@link ExperimentTypeVisitor} object
     * @param parameter param
     * @param <T> - result generic type
     * @param <P> - param generic type
     * @return result generic class
     */
    public <T, P> T handle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
        T result = internalHandle(visitor, parameter);
        visitor.afterHandle(result, parameter);
        return result;
    }

    /**
     * Visitor pattern internal handling method.
     * @param visitor {@link ExperimentTypeVisitor} object
     * @param parameter param
     * @param <T> - result generic type
     * @param <P> - param generic type
     * @return result generic class
     */
    public abstract <T, P> T internalHandle(ExperimentTypeVisitor<T, P> visitor, P parameter);
}
