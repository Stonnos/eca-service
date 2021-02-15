package com.ecaservice.model.entity;

import com.ecaservice.model.dictionary.RequestStatusDictionary;
import eca.core.DescriptiveEnum;
import lombok.RequiredArgsConstructor;

/**
 * Request status enum.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public enum RequestStatus implements DescriptiveEnum {

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
    },

    /**
     * In progress status
     */
    IN_PROGRESS(RequestStatusDictionary.IN_PROGRESS_STATUS_DESCRIPTION) {
        @Override
        public <T, P> T handle(RequestStatusVisitor<T, P> visitor, P parameter) {
            return visitor.caseInProgress(parameter);
        }
    };

    private final String description;

    /**
     * Returns request status description.
     *
     * @return request status description
     */
    @Override
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
