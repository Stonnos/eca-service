package com.ecaservice.server.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
