package com.ecaservice.base.model;

import com.ecaservice.base.model.visitor.EcaRequestVisitor;

/**
 * Eca request marker interface.
 *
 * @author Roman Batygin
 */
public interface EcaRequest {

    /**
     * Applies visitor.
     *
     * @param ecaRequestVisitor - eca request visitor
     */
    void visit(EcaRequestVisitor ecaRequestVisitor);
}
