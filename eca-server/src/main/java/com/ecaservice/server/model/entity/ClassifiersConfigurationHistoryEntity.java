package com.ecaservice.server.model.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Classifiers configuration history persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "classifiers_configuration_history")
public class ClassifiersConfigurationHistoryEntity {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Created at
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * User name
     */
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    /**
     * Action type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ClassifiersConfigurationActionType actionType;

    /**
     * Message text
     */
    @Column(name = "message_text", nullable = false, columnDefinition = "text")
    private String messageText;

    /**
     * Classifiers configuration.
     */
    @ManyToOne
    @JoinColumn(name = "configuration_id", nullable = false)
    private ClassifiersConfiguration configuration;
}
