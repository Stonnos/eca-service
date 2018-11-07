package com.ecaservice.model.entity;

/**
 * Request status enum.
 *
 * @author Roman Batygin
 */
public enum RequestStatus {

    /**
     * New status
     */
    NEW {
        @Override
        public <T, P> T handle(RequestStatusVisitor<T, P> visitor, P parameter) {
            return visitor.caseNew(parameter);
        }
    },

    /**
     * Finished status
     */
    FINISHED {
        @Override
        public <T, P> T handle(RequestStatusVisitor<T, P> visitor, P parameter) {
            return visitor.caseFinished(parameter);
        }
    },

    /**
     * Timeout status
     */
    TIMEOUT {
        @Override
        public <T, P> T handle(RequestStatusVisitor<T, P> visitor, P parameter) {
            return visitor.caseTimeout(parameter);
        }
    },

    /**
     * Error status
     */
    ERROR {
        @Override
        public <T, P> T handle(RequestStatusVisitor<T, P> visitor, P parameter) {
            return visitor.caseError(parameter);
        }
    };

    /**
     * Visitor pattern common method.
     *
     * @param visitor   - {@link RequestStatusVisitor} object
     * @param parameter - param object
     * @param <T>       - result generic type
     * @param <P>       - param generic type
     * @return result generic class
     */
    public abstract <T, P> T handle(RequestStatusVisitor<T, P> visitor, P parameter);
}
