package com.ecaservice.web.dto.model;

import com.ecaservice.web.dto.MatchModeVisitor;

/**
 * Filter match mode type.
 *
 * @author Roman Batygin
 */
public enum MatchMode {

    /**
     * Equals match mode
     */
    EQUALS {
        @Override
        public <T> T handle(MatchModeVisitor<T> matchModeVisitor) {
            return matchModeVisitor.caseEquals();
        }
    },

    /**
     * Like match mode
     */
    LIKE {
        @Override
        public <T> T handle(MatchModeVisitor<T> matchModeVisitor) {
            return matchModeVisitor.caseLike();
        }
    },

    /**
     * Range match mode
     */
    RANGE {
        @Override
        public <T> T handle(MatchModeVisitor<T> matchModeVisitor) {
            return matchModeVisitor.caseRange();
        }
    };

    /**
     * Visitor pattern common method
     *
     * @param matchModeVisitor - match mode visitor
     * @param <T>              - generic type
     * @return generic object
     */
    public abstract <T> T handle(MatchModeVisitor<T> matchModeVisitor);
}
