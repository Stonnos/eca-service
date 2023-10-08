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
    default void visitWeb() {}

    /**
     * Visit queue channel.
     */
    default void visitQueue() {}
}
