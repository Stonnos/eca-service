package com.ecaservice.auto.test.entity.autotest;

import com.ecaservice.auto.test.model.EmailType;
import com.ecaservice.test.common.model.MatchResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import static com.ecaservice.auto.test.util.Constraints.DOWNLOAD_URL_MAX_LENGTH;

/**
 * Email test step persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@DiscriminatorValue("EMAIL_STEP")
public class EmailTestStepEntity extends BaseTestStepEntity<ExperimentRequestEntity> {

    /**
     * Email type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "email_type")
    private EmailType emailType;

    /**
     * Message received?
     */
    @Column(name = "message_received")
    private boolean messageReceived;

    /**
     * Expected download url
     */
    @Column(name = "expected_download_url", length = DOWNLOAD_URL_MAX_LENGTH)
    private String expectedDownloadUrl;

    /**
     * Actual download url
     */
    @Column(name = "actual_download_url", length = DOWNLOAD_URL_MAX_LENGTH)
    private String actualDownloadUrl;

    /**
     * Download url match result
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "download_url_match_result")
    private MatchResult downloadUrlMatchResult;

    @Override
    public void visit(TestStepVisitor visitor) {
        visitor.visit(this);
    }
}
