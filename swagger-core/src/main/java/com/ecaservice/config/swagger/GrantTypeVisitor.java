package com.ecaservice.config.swagger;

public interface GrantTypeVisitor {

    void visitPassword();

    void visitClientCredentials();
}
