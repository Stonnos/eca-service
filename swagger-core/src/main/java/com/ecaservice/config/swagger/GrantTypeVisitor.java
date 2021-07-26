package com.ecaservice.config.swagger;

/**
 * Grant type visitor.
 *
 * @author Roman Batygin
 */
public interface GrantTypeVisitor {

    /**
     * Visit password grant type.
     */
    void visitPassword();

    /**
     * Visit client credentials grant type.
     */
    void visitClientCredentials();
}
