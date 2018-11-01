package com.ecaservice.web.dto;

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
     * Less or equals than match mode
     */
    LTE {
        @Override
        public <T> T handle(MatchModeVisitor<T> matchModeVisitor) {
            return matchModeVisitor.caseLte();
        }
    },

    /**
     * Greater or equals than match mode
     */
    GTE {
        @Override
        public <T> T handle(MatchModeVisitor<T> matchModeVisitor) {
            return matchModeVisitor.caseGte();
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
