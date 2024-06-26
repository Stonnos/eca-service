package com.ecaservice.ers.filter;

import com.ecaservice.ers.dto.EvaluationMethod;
import com.ecaservice.ers.dto.EvaluationMethodReport;
import com.ecaservice.ers.model.EvaluationResultsInfo;
import com.ecaservice.ers.model.InstancesInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

import static com.ecaservice.ers.model.EvaluationResultsInfo_.EVALUATION_METHOD;
import static com.ecaservice.ers.model.EvaluationResultsInfo_.INSTANCES_INFO;
import static com.ecaservice.ers.model.EvaluationResultsInfo_.NUM_FOLDS;
import static com.ecaservice.ers.model.EvaluationResultsInfo_.NUM_TESTS;
import static com.ecaservice.ers.model.EvaluationResultsInfo_.SEED;
import static com.ecaservice.ers.model.InstancesInfo_.ID;
import static com.ecaservice.ers.util.Utils.toInteger;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Evaluation result filter class.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class OptimalEvaluationResultsFilter implements Specification<EvaluationResultsInfo> {

    /**
     * Instances info id
     */
    @Getter
    private final InstancesInfo instancesInfo;

    /**
     * Evaluation method report
     */
    @Getter
    private final EvaluationMethodReport evaluationMethodReport;

    @Override
    public Predicate toPredicate(Root<EvaluationResultsInfo> root, CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = newArrayList();
        Join<EvaluationResultsInfo, InstancesInfo> join = root.join(INSTANCES_INFO);
        predicates.add(criteriaBuilder.equal(join.get(ID), instancesInfo.getId()));
        predicates.add(criteriaBuilder.equal(root.get(EVALUATION_METHOD),
                evaluationMethodReport.getEvaluationMethod()));
        if (EvaluationMethod.CROSS_VALIDATION.equals(evaluationMethodReport.getEvaluationMethod())) {
            Optional.ofNullable(evaluationMethodReport.getNumFolds()).ifPresent(numFolds -> predicates.add(
                    criteriaBuilder.equal(root.get(NUM_FOLDS), toInteger(evaluationMethodReport.getNumFolds()))));
            Optional.ofNullable(evaluationMethodReport.getNumFolds()).ifPresent(numFolds -> predicates.add(
                    criteriaBuilder.equal(root.get(NUM_TESTS), toInteger(evaluationMethodReport.getNumTests()))));
            Optional.ofNullable(evaluationMethodReport.getNumFolds()).ifPresent(numFolds -> predicates.add(
                    criteriaBuilder.equal(root.get(SEED), toInteger(evaluationMethodReport.getSeed()))));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
