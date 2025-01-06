package com.ecaservice.core.transactional.outbox.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Outbox message persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "outbox_message")
public class OutboxMessageEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Message code
     */
    @Column(name = "message_code", nullable = false)
    private String messageCode;

    /**
     * Message body
     */
    @Column(name = "message_body", columnDefinition = "text", nullable = false)
    private String messageBody;

    /**
     * Created date
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Lock time to live date
     */
    @Column(name = "locked_ttl")
    private LocalDateTime lockedTtl;
}
