package com.ecaservice.core.message.template.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Message template persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "message_template")
public class MessageTemplateEntity {

    /**
     * Template id
     */
    @Id
    private String id;

    /**
     * Template text
     */
    @Column(name = "template_text", columnDefinition = "text", nullable = false)
    private String templateText;
}
