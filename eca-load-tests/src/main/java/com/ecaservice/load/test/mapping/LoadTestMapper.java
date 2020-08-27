package com.ecaservice.load.test.mapping;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.report.bean.EvaluationTestBean;
import com.ecaservice.load.test.report.bean.LoadTestBean;
import eca.core.evaluation.EvaluationMethod;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.ecaservice.load.test.util.Utils.totalTime;

/**
 * Load test mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class LoadTestMapper {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String CV_FORMAT = "%d - блочная кросс - проверка";
    private static final String CV_EXTENDED_FORMAT = "%d×%d - блочная кросс - проверка";

    /**
     * Maps load test entity to evaluation request.
     *
     * @param loadTestEntity - load test entity
     * @return evaluation request
     */
    public abstract EvaluationRequest map(LoadTestEntity loadTestEntity);

    /**
     * Maps load test entity to load test bean.
     *
     * @param loadTestEntity - load test entity
     * @return load test bean
     */
    @Mapping(source = "started", target = "started", qualifiedByName = "formatLocalDateTime")
    @Mapping(source = "finished", target = "finished", qualifiedByName = "formatLocalDateTime")
    @Mapping(target = "evaluationMethod", ignore = true)
    public abstract LoadTestBean mapToBean(LoadTestEntity loadTestEntity);

    /**
     * Maps evaluation request to evaluation test bean.
     *
     * @param evaluationRequestEntity - evaluation request entity
     * @return evaluation test bean
     */
    @Mapping(source = "started", target = "started", qualifiedByName = "formatLocalDateTime")
    @Mapping(source = "finished", target = "finished", qualifiedByName = "formatLocalDateTime")
    public abstract EvaluationTestBean map(EvaluationRequestEntity evaluationRequestEntity);

    @AfterMapping
    protected void mapEvaluationMethod(LoadTestEntity loadTestEntity, @MappingTarget LoadTestBean loadTestBean) {
        if (EvaluationMethod.CROSS_VALIDATION.equals(loadTestEntity.getEvaluationMethod())) {
            String crossValidationMethodDetails;
            if (loadTestEntity.getNumTests() == 1) {
                crossValidationMethodDetails = String.format(CV_FORMAT, loadTestEntity.getNumFolds());
            } else {
                crossValidationMethodDetails =
                        String.format(CV_EXTENDED_FORMAT, loadTestEntity.getNumFolds(), loadTestEntity.getNumTests());
            }
            loadTestBean.setEvaluationMethod(crossValidationMethodDetails);
        }
        loadTestBean.setEvaluationMethod(loadTestEntity.getEvaluationMethod().getDescription());
    }

    @AfterMapping
    protected void mapTotalTime(LoadTestEntity loadTestEntity, @MappingTarget LoadTestBean loadTestBean) {
        loadTestBean.setTotalTime(totalTime(loadTestEntity.getStarted(), loadTestEntity.getFinished()));
    }

    @AfterMapping
    protected void mapTotalTime(EvaluationRequestEntity evaluationRequestEntity,
                                @MappingTarget EvaluationTestBean evaluationTestBean) {
        evaluationTestBean.setTotalTime(
                totalTime(evaluationRequestEntity.getStarted(), evaluationRequestEntity.getFinished()));
    }

    @Named("formatLocalDateTime")
    protected String formatLocalDateTime(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime).map(dateTimeFormatter::format).orElse(null);
    }
}
