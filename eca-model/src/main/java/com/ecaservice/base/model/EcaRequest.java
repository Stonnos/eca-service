package com.ecaservice.base.model;

import com.ecaservice.base.model.visitor.EcaRequestVisitor;
import weka.core.Instances;

/**
 * Eca request marker interface.
 *
 * @author Roman Batygin
 */
public interface EcaRequest {

    /**
     * Gets instances.
     *
     * @return instances
     */
    Instances getData();

    /**
     * Applies visitor.
     *
     * @param ecaRequestVisitor - eca request visitor
     */
    void visit(EcaRequestVisitor ecaRequestVisitor);
}
