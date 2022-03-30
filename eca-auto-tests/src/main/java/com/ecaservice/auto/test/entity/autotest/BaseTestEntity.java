package com.ecaservice.auto.test.entity.autotest;

import com.ecaservice.test.common.model.TestResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

/**
 * Base test entity model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@MappedSuperclass
public class BaseTestEntity extends BaseEntity {

    /**
     * Test result
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "test_result")
    private TestResult testResult;

    /**
     * Total matched
     */
    @Column(name = "total_matched")
    private int totalMatched;

    /**
     * Total not matched
     */
    @Column(name = "total_not_matched")
    private int totalNotMatched;

    /**
     * Total not found
     */
    @Column(name = "total_not_found")
    private int totalNotFound;
}
