package com.ecaservice.load.test.mapping;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.report.bean.EvaluationTestBean;
import com.ecaservice.load.test.report.bean.LoadTestBean;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Load test mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class LoadTestMapper {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    /**
     * Maps evaluation requests entities list to evaluation tests beans.
     *
     * @param evaluationRequestEntities - evaluation requests entities list
     * @return evaluation tests beans list
     */
    public abstract List<EvaluationTestBean> map(List<EvaluationRequestEntity> evaluationRequestEntities);

    @Named("formatLocalDateTime")
    protected String formatLocalDateTime(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime).map(dateTimeFormatter::format).orElse(null);
    }
}
