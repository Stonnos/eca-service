package com.ecaservice.auto.test.entity.ecaserver;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Evaluation log persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "evaluation_log")
public class EvaluationLog extends AbstractEvaluationEntity {
}
