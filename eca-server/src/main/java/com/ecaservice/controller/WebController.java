package com.ecaservice.controller;

import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.specification.Filter;
import com.ecaservice.util.SortUtils;
import com.ecaservice.util.Utils;
import com.ecaservice.web.dto.EvaluationLogDto;
import com.ecaservice.web.dto.ExperimentDto;
import com.ecaservice.web.dto.ExperimentPageDto;
import com.ecaservice.web.dto.PageRequestDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

/**
 * Rest controller providing operations for web application.
 */
@Api(tags = "Operations for web application")
@Slf4j
@RestController
public class WebController {

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

    /**
     * Finds experiments with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return experiment page dto
     */
    @ApiOperation(
            value = "Finds experiments with specified options",
            notes = "Finds experiments with specified options"
    )
    @GetMapping(value = "/experiments")
    public ExperimentPageDto getExperiments(PageRequestDto pageRequestDto) {
        Sort sort = SortUtils.buildSort(pageRequestDto.getSortField(), pageRequestDto.isAscending());
        Filter<Experiment> filter = new Filter<>(Experiment.class, pageRequestDto.getFilters());
        Page<Experiment> experiments = experimentRepository.findAll(filter,
                PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort));
        return buildExperimentPageDto(experiments);
    }

    @ApiOperation(
            value = "Finds evaluation logs with specified options",
            notes = "Finds evaluation logs with specified options"
    )
    @GetMapping(value = "/evaluations")
    public List<EvaluationLogDto> getEvaluationLogs() {
        return null;
    }

    private ExperimentPageDto buildExperimentPageDto(Page<Experiment> experimentPage) {
        List<Experiment> experiments = experimentPage.getContent();
        List<ExperimentDto> experimentDtoList = experimentMapper.map(experiments);
        ExperimentPageDto experimentPageDto = new ExperimentPageDto();
        experimentPageDto.setContent(experimentDtoList);
        experimentPageDto.setTotalCount(experimentPage.getTotalElements());
        experimentPageDto.setNewExperimentsCount(Utils.getExperimentsCouunt(experiments, ExperimentStatus.NEW));
        experimentPageDto.setFinishedExperimentsCount(
                Utils.getExperimentsCouunt(experiments, ExperimentStatus.FINISHED));
        experimentPageDto.setTimeoutExperimentsCount(Utils.getExperimentsCouunt(experiments, ExperimentStatus.TIMEOUT));
        experimentPageDto.setErrorExperimentsCount(Utils.getExperimentsCouunt(experiments, ExperimentStatus.ERROR));
        return experimentPageDto;
    }
}
