package com.ecaservice.ers.filter;

import com.ecaservice.ers.dto.EvaluationMethod;
import com.ecaservice.ers.dto.EvaluationMethodReport;
import com.ecaservice.ers.model.EvaluationResultsInfo;
import com.ecaservice.ers.model.InstancesInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

import static com.ecaservice.ers.model.EvaluationResultsInfo_.EVALUATION_METHOD;
import static com.ecaservice.ers.model.EvaluationResultsInfo_.INSTANCES;
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
public class EvaluationResultsFilter implements Specification<EvaluationResultsInfo> {

    /**
     * Instances info id
     */
    @Getter
    private final Long instancesInfoId;

    /**
     * Evaluation method report
     */
    @Getter
    private final EvaluationMethodReport evaluationMethodReport;

    @Override
    public Predicate toPredicate(Root<EvaluationResultsInfo> root, CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = newArrayList();
        Join<EvaluationResultsInfo, InstancesInfo> join = root.join(INSTANCES);
        predicates.add(criteriaBuilder.equal(join.get(ID), instancesInfoId));
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
