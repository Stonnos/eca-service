package com.ecaservice.ers.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Evaluation results sort persistence entity.
 *
 * @author Roman Batygin
 */
@Data
@Builder
@Entity
@Table(name = "evaluation_results_sort", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"field_name", "field_order"}, name = "field_name_field_order_unique_index")})
public class EvaluationResultsSortEntity {

    @Tolerate
    public EvaluationResultsSortEntity() {
        //default constructor
    }

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Sort field name
     */
    @Column(name = "field_name", nullable = false)
    private String fieldName;

    /**
     * Is ascending?
     */
    @Column(name = "is_ascending")
    private boolean ascending;

    /**
     * Field order
     */
    @Column(name = "field_order", nullable = false)
    private int fieldOrder;
}
