package com.ecaservice.server.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

/**
 * Classifiers configuration history persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "classifiers_configuration_history")
public class ClassifiersConfigurationHistoryEntity extends BaseEntity {

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
