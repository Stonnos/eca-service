package com.ecaservice.server.model.entity;

/**
 * Channel visitor interface.
 *
 * @author Roman Batygin
 */
public interface ChannelVisitor {

    /**
     * Visit web channel.
     */
    void visitWeb();

    /**
     * Visit queue channel.
     */
    void visitQueue();
}
