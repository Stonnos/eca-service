package com.ecaservice.controller;

import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.util.SortUtils;
import com.ecaservice.web.dto.EvaluationLogDto;
import com.ecaservice.web.dto.ExperimentDto;
import com.ecaservice.web.dto.PageDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;

/**
 * Rest controller providing operations for web application.
 */
@Api(tags = "Operations for web application")
@Slf4j
@RestController
public class WebController {

    private static final String EXPERIMENT_DEFAULT_SORT_FIELD = "creationDate";

    private final ExperimentMapper experimentMapper;
    private final ExperimentRepository experimentRepository;
    private final EvaluationLogRepository evaluationLogRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param experimentMapper        - experiment mapper bean
     * @param experimentRepository    - experiment repository bean
     * @param evaluationLogRepository - evaluation log repository bean
     */
    @Inject
    public WebController(ExperimentMapper experimentMapper,
                         ExperimentRepository experimentRepository,
                         EvaluationLogRepository evaluationLogRepository) {
        this.experimentMapper = experimentMapper;
        this.experimentRepository = experimentRepository;
        this.evaluationLogRepository = evaluationLogRepository;
    }

    @ApiOperation(
            value = "Finds experiments with specified options",
            notes = "Finds experiments with specified options"
    )
    @GetMapping(value = "/experiments")
    public PageDto<ExperimentDto> getExperiments(@RequestParam int page,
                                                 @RequestParam int size,
                                                 @RequestParam String sortField,
                                                 @RequestParam boolean ascending) {
        Sort sort = SortUtils.buildSort(sortField, ascending, EXPERIMENT_DEFAULT_SORT_FIELD);
        Page<Experiment> experiments = experimentRepository.findAll(PageRequest.of(page, size, sort));
        List<ExperimentDto> experimentDtoList = experimentMapper.map(experiments.getContent());
        return PageDto.of(experimentDtoList, experiments.getTotalElements());
    }

    @ApiOperation(
            value = "Finds evaluation logs with specified options",
            notes = "Finds evaluation logs with specified options"
    )
    @GetMapping(value = "/evaluations")
    public List<EvaluationLogDto> getEvaluationLogs() {
        return null;
    }
}
