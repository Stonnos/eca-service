package com.ecaservice.model.entity;

import com.ecaservice.model.evaluation.EvaluationMethod;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Classifier options request model to ERS service.
 *
 * @author Roman Batygin
 */
@Data
@Entity
@Table(name = "classifier_options_request_model")
public class ClassifierOptionsRequestModel extends ErsRequest {

    /**
     * Training data MD5 hash
     */
    @Column(name = "data_md5_hash")
    private String dataMd5Hash;

    /**
     * Evaluation method
     */
    @Column(name = "evaluation_method")
    private EvaluationMethod evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    @Column(name = "num_folds")
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @Column(name = "num_tests")
    private Integer numTests;

    /**
     * Seed value for k * V cross - validation method
     */
    private Integer seed;

    /**
     * Classifier options response
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "classifier_options_response_model_id", nullable = false)
    private List<ClassifierOptionsResponseModel> classifierOptionsResponseModels;

}
