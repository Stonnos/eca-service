package com.ecaservice.auto.test.entity.autotest;

import com.ecaservice.auto.test.model.EmailType;
import com.ecaservice.test.common.model.MatchResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Email test step persistence entity.
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@DiscriminatorValue("EMAIL_STEP")
public class EmailTestStepEntity extends BaseTestStepEntity {

    /**
     * Email type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "email_type", nullable = false)
    private EmailType emailType;

    /**
     * Message received?
     */
    @Column(name = "message_received")
    private boolean messageReceived;

    /**
     * Expected download url
     */
    private String expectedDownloadUrl;

    /**
     * Actual download url
     */
    private String actualDownloadUrl;

    /**
     * Download url match result
     */
    @Enumerated(EnumType.STRING)
    private MatchResult downloadUrlMatchResult;
}
