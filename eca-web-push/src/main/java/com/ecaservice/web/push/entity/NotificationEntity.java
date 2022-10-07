package com.ecaservice.web.push.entity;

import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Notification persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "notification")
public class NotificationEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Request id
     */
    @Column(name = "request_id", nullable = false, unique = true)
    private String requestId;

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
    @Column(nullable = false)
    private String initiator;

    /**
     * Receiver user
     */
    @Column(nullable = false)
    private String receiver;

    /**
     * Created date
     */
    @Column(nullable = false)
    private LocalDateTime created;

    /**
     * Notification parameters list
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "notification_id", nullable = false)
    private List<NotificationParameter> parameters;
}
