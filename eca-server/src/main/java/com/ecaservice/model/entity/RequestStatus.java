package com.ecaservice.model.entity;

import com.ecaservice.model.dictionary.RequestStatusDictionary;

/**
 * Request status enum.
 *
 * @author Roman Batygin
 */
public enum RequestStatus {

    /**
     * New status
     */
    NEW(RequestStatusDictionary.NEW_STATUS_DESCRIPTION) {
        @Override
        public <T, P> T handle(RequestStatusVisitor<T, P> visitor, P parameter) {
            return visitor.caseNew(parameter);
        }
    },

    /**
     * Finished status
     */
    FINISHED(RequestStatusDictionary.FINISHED_STATUS_DESCRIPTION) {
        @Override
        public <T, P> T handle(RequestStatusVisitor<T, P> visitor, P parameter) {
            return visitor.caseFinished(parameter);
        }
    },

    /**
     * Timeout status
     */
    TIMEOUT(RequestStatusDictionary.TIMEOUT_STATUS_DESCRIPTION) {
        @Override
        public <T, P> T handle(RequestStatusVisitor<T, P> visitor, P parameter) {
            return visitor.caseTimeout(parameter);
        }
    },

    /**
     * Error status
     */
    ERROR(RequestStatusDictionary.ERROR_STATUS_DESCRIPTION) {
        @Override
        public <T, P> T handle(RequestStatusVisitor<T, P> visitor, P parameter) {
            return visitor.caseError(parameter);
        }
    };

    private String description;

    RequestStatus(String description) {
        this.description = description;
    }

    /**
     * Returns request status description.
     *
     * @return request status description
     */
    public String getDescription() {
        return description;
    }

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
