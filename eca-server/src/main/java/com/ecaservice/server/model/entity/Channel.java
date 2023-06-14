package com.ecaservice.server.model.entity;

/**
 * Channel type enum.
 *
 * @author Roman Batygin
 */
public enum Channel {

    /**
     * Queue channel
     */
    QUEUE {
        @Override
        public void visit(ChannelVisitor visitor) {
            visitor.visitQueue();
        }
    },

    /**
     * Web channel
     */
    WEB {
        @Override
        public void visit(ChannelVisitor visitor) {
            visitor.visitWeb();
        }
    };

    /**
     * Invokes visitor.
     *
     * @param visitor - visitor interface
     */
    public abstract void visit(ChannelVisitor visitor);
}
