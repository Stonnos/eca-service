package com.ecaservice.model;

/**
 * Experiment type.
 *
 * @author Roman Batygin
 */
public enum ExperimentType {

    NEURAL_NETWORKS {
        @Override
        public <T, P> T handle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseNeuralNetwork(parameter);
        }
    },

    HETEROGENEOUS_ENSEMBLE {
        @Override
        public <T, P> T handle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseHeterogeneousEnsemble(parameter);
        }
    },

    MODIFIED_HETEROGENEOUS_ENSEMBLE {
        @Override
        public <T, P> T handle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseModifiedHeterogeneousEnsemble(parameter);
        }
    },

    ADA_BOOST {
        @Override
        public <T, P> T handle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseAdaBoost(parameter);
        }
    },

    STACKING {
        @Override
        public <T, P> T handle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseStacking(parameter);
        }
    },

    KNN {
        @Override
        public <T, P> T handle(ExperimentTypeVisitor<T, P> visitor, P parameter) {
            return visitor.caseKNearestNeighbours(parameter);
        }
    };

    /**
     * Visitor pattern common method
     *
     * @param visitor visitor class
     * @throws Exception
     */
    public abstract <T, P> T handle(ExperimentTypeVisitor<T, P> visitor, P parameter);
}
