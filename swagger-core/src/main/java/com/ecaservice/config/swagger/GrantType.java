package com.ecaservice.config.swagger;

/**
 * Oauth2 grant type.
 *
 * @author Roman Batygin
 */
public enum GrantType {

    /**
     * Password grant
     */
    PASSWORD {
        @Override
        public void handle(GrantTypeVisitor visitor) {
            visitor.visitPassword();
        }
    },

    /**
     * Client credentials grant
     */
    CLIENT_CREDENTIALS {
        @Override
        public void handle(GrantTypeVisitor visitor) {
            visitor.visitClientCredentials();
        }
    };

    /**
     * Visitor method.
     *
     * @param visitor - visitor interface
     */
    public abstract void handle(GrantTypeVisitor visitor);
}
