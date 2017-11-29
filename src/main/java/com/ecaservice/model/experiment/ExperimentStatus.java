package com.ecaservice.model.experiment;

/**
 * Experiment status enum.
 *
 * @author Roman Batygin
 */
public enum ExperimentStatus {

    /**
     * New status
     */
    NEW {
        @Override
        public <T, P> T handle(ExperimentStatusVisitor<T, P> visitor, P parameter) {
            return visitor.caseNew(parameter);
        }
    },

    /**
     * Finished status
     */
    FINISHED {
        @Override
        public <T, P> T handle(ExperimentStatusVisitor<T, P> visitor, P parameter) {
            return visitor.caseFinished(parameter);
        }
    },

    /**
     * Timeout status
     */
    TIMEOUT {
        @Override
        public <T, P> T handle(ExperimentStatusVisitor<T, P> visitor, P parameter) {
            return visitor.caseTimeout(parameter);
        }
    },

    /**
     * Error status
     */
    ERROR {
        @Override
        public <T, P> T handle(ExperimentStatusVisitor<T, P> visitor, P parameter) {
            return visitor.caseError(parameter);
        }
    },

    /**
     * Exceeded status
     */
    EXCEEDED {
        @Override
        public <T, P> T handle(ExperimentStatusVisitor<T, P> visitor, P parameter) {
            return visitor.caseExceeded(parameter);
        }
    };

    /**
     * Visitor pattern common method.
     *
     * @param visitor   {@link ExperimentStatus} object
     * @param parameter param
     * @param <T>       - result generic type
     * @param <P>       - param generic type
     * @return result generic class
     */
    public abstract <T, P> T handle(ExperimentStatusVisitor<T, P> visitor, P parameter);
}
