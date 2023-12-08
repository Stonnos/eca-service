package com.ecaservice.ers.service;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.filter.validation.annotations.ValidPageRequest;
import com.ecaservice.ers.filter.EvaluationResultsHistoryFilter;
import com.ecaservice.ers.mapping.EvaluationResultsMapper;
import com.ecaservice.ers.repository.EvaluationResultsInfoRepository;
import com.ecaservice.web.dto.model.EvaluationResultsHistoryDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.ers.dictionary.FilterDictionaries.EVALUATION_RESULTS_HISTORY_TEMPLATE;
import static com.ecaservice.ers.model.EvaluationResultsInfo_.SAVE_DATE;

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

    private final FilterTemplateService filterTemplateService;
    private final EvaluationResultsMapper evaluationResultsMapper;
    private final EvaluationResultsInfoRepository evaluationResultsInfoRepository;


    /**
     * Gets evaluation results history page.
     *
     * @param pageRequestDto - page request dto
     * @return evaluation results history page
     */
    public PageDto<EvaluationResultsHistoryDto> getNextPage(
            @ValidPageRequest(filterTemplateName = EVALUATION_RESULTS_HISTORY_TEMPLATE) PageRequestDto pageRequestDto) {
        Sort sort = buildSort(pageRequestDto.getSortField(), SAVE_DATE, pageRequestDto.isAscending());
        List<String> globalFilterFields =
                filterTemplateService.getGlobalFilterFields(EVALUATION_RESULTS_HISTORY_TEMPLATE);
        var filter = new EvaluationResultsHistoryFilter(pageRequestDto.getSearchQuery(), globalFilterFields,
                pageRequestDto.getFilters());
        var pageRequest = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort);
        var evaluationResultsInfoPage =
                evaluationResultsInfoRepository.findAll(filter, pageRequest);
        var evaluationResultsDtoList = evaluationResultsInfoPage.getContent()
                .stream()
                .map(evaluationResultsMapper::mapToEvaluationResultsHistory)
                .collect(Collectors.toList());
        log.info("Evaluation results page [{} of {}] with size [{}] has been fetched for page request [{}]",
                evaluationResultsInfoPage.getNumber(), evaluationResultsInfoPage.getTotalPages(),
                evaluationResultsInfoPage.getNumberOfElements(), pageRequestDto);
        return PageDto.of(evaluationResultsDtoList, pageRequestDto.getPage(),
                evaluationResultsInfoPage.getTotalElements());
    }
}
