package com.ecaservice.ers.service;

import com.ecaservice.classifier.template.processor.service.ClassifierOptionsProcessor;
import com.ecaservice.core.filter.query.FilterQueryExecutor;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.filter.specification.FilterFieldCustomizer;
import com.ecaservice.core.filter.validation.annotations.ValidPageRequest;
import com.ecaservice.ers.config.ErsConfig;
import com.ecaservice.ers.filter.ClassifierNameFilterFieldCustomizer;
import com.ecaservice.ers.filter.EvaluationResultsHistoryFilter;
import com.ecaservice.ers.mapping.EvaluationResultsMapper;
import com.ecaservice.ers.model.EvaluationResultsInfo;
import com.ecaservice.web.dto.model.EvaluationResultsHistoryDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.classifier.template.processor.util.Utils.parseOptions;
import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.ers.dictionary.FilterDictionaries.EVALUATION_RESULTS_HISTORY_TEMPLATE;
import static com.ecaservice.ers.model.EvaluationResultsInfo_.SAVE_DATE;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Implements evaluation results history service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class EvaluationResultsHistoryService {

    private final ErsConfig ersConfig;
    private final FilterTemplateService filterTemplateService;
    private final EvaluationResultsMapper evaluationResultsMapper;
    private final ClassifierOptionsProcessor classifierOptionsProcessor;
    private final EntityManager entityManager;
    private final EvaluationResultsHistoryCountQueryExecutor evaluationResultsHistoryCountQueryExecutor;

    private final List<FilterFieldCustomizer> globalFilterFieldCustomizers = newArrayList();

    /**
     * Initialization method.
     */
    @PostConstruct
    public void initialize() {
        globalFilterFieldCustomizers.add(new ClassifierNameFilterFieldCustomizer(filterTemplateService));
    }

    /**
     * Gets evaluation results history page.
     *
     * @param pageRequestDto - page request dto
     * @return evaluation results history page
     */
    public Page<EvaluationResultsInfo> getEvaluationResultsInfoPage(
            @ValidPageRequest(filterTemplateName = EVALUATION_RESULTS_HISTORY_TEMPLATE) PageRequestDto pageRequestDto) {
        Sort sort = buildSort(pageRequestDto.getSortFields(), SAVE_DATE, true);
        List<String> globalFilterFields =
                filterTemplateService.getGlobalFilterFields(EVALUATION_RESULTS_HISTORY_TEMPLATE);
        var filter = new EvaluationResultsHistoryFilter(pageRequestDto.getSearchQuery(), globalFilterFields,
                pageRequestDto.getFilters());
        filter.setGlobalFilterFieldsCustomizers(globalFilterFieldCustomizers);
        var queryExecutor = new FilterQueryExecutor(entityManager);
        var pageRequest = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort);
        var evaluationResultsInfoPage = queryExecutor.executePageQuery(pageRequestDto, filter, pageRequest,
                evaluationResultsHistoryCountQueryExecutor);
        log.info("Evaluation results page [{} of {}] with size [{}] has been fetched for page request [{}]",
                evaluationResultsInfoPage.getNumber(), evaluationResultsInfoPage.getTotalPages(),
                evaluationResultsInfoPage.getNumberOfElements(), pageRequestDto);
        return evaluationResultsInfoPage;
    }

    /**
     * Gets evaluation results history page.
     *
     * @param pageRequestDto - page request dto
     * @return evaluation results history page
     */
    public PageDto<EvaluationResultsHistoryDto> getNextPage(
            @ValidPageRequest(filterTemplateName = EVALUATION_RESULTS_HISTORY_TEMPLATE)
            PageRequestDto pageRequestDto) {
        var evaluationResultsInfoPage = getEvaluationResultsInfoPage(pageRequestDto);
        var evaluationResultsDtoList =
                mapToEvaluationResultsHistoryList(evaluationResultsInfoPage.getContent());
        log.info("Evaluation results page [{} of {}] with size [{}] has been fetched for page request [{}]",
                evaluationResultsInfoPage.getNumber(), evaluationResultsInfoPage.getTotalPages(),
                evaluationResultsInfoPage.getNumberOfElements(), pageRequestDto);
        long totalElements = Long.min(evaluationResultsInfoPage.getTotalElements(),
                pageRequestDto.getSize() * ersConfig.getMaxPagesNum());
        return PageDto.of(evaluationResultsDtoList, pageRequestDto.getPage(), totalElements);
    }

    private List<EvaluationResultsHistoryDto> mapToEvaluationResultsHistoryList(
            List<EvaluationResultsInfo> evaluationResultsInfoList) {
        return evaluationResultsInfoList.stream()
                .map(evaluationResultsInfo -> {
                    var evaluationResultsHistoryDto =
                            evaluationResultsMapper.mapToEvaluationResultsHistory(evaluationResultsInfo);
                    var classifierOptions =
                            parseOptions(evaluationResultsInfo.getClassifierOptions());
                    var classifierInfoDto =
                            classifierOptionsProcessor.processClassifierOptions(classifierOptions);
                    evaluationResultsHistoryDto.setClassifierInfo(classifierInfoDto);
                    return evaluationResultsHistoryDto;
                })
                .collect(Collectors.toList());
    }
}
