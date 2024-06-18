package com.ecaservice.web.push.entity;

import lombok.Data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Notification persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "notification", indexes = @Index(columnList = "receiver", name = "notification_receiver_idx"))
public class NotificationEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Message type
     */
    @Column(name = "message_type", nullable = false)
    private String messageType;

    /**
     * Message text
     */
    @Column(name = "message_text")
    private String messageText;

    /**
     * Initiator user
     */
    private String initiator;

    /**
     * Receiver user
     */
    @Column(nullable = false)
    private String receiver;

    /**
     * Message status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "message_status", nullable = false)
    private MessageStatus messageStatus;

    /**
     * Created date
     */
    @Column(nullable = false)
    private LocalDateTime created;

    /**
     * Notification parameters list
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "notification_id", nullable = false)
    private List<NotificationParameter> parameters;
}
