package com.ecaservice.mapping.report;

import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.report.model.EvaluationLogBean;
import com.ecaservice.report.model.ExperimentBean;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Base report mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class BaseReportMapper {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Maps experiment entity to experiment report bean.
     *
     * @param experiment - experiment entity
     * @return experiment bean
     */
    @Mapping(source = "experimentAbsolutePath", target = "experimentAbsolutePath", qualifiedByName = "toFileName")
    @Mapping(source = "trainingDataAbsolutePath", target = "trainingDataAbsolutePath", qualifiedByName = "toFileName")
    @Mapping(source = "evaluationMethod.description", target = "evaluationMethod")
    @Mapping(source = "experimentType.description", target = "experimentType")
    @Mapping(source = "experimentStatus.description", target = "experimentStatus")
    @Mapping(source = "creationDate", target = "creationDate", qualifiedByName = "formatLocalDateTime")
    @Mapping(source = "startDate", target = "startDate", qualifiedByName = "formatLocalDateTime")
    @Mapping(source = "endDate", target = "endDate", qualifiedByName = "formatLocalDateTime")
    @Mapping(source = "sentDate", target = "sentDate", qualifiedByName = "formatLocalDateTime")
    @Mapping(source = "deletedDate", target = "deletedDate", qualifiedByName = "formatLocalDateTime")
    public abstract ExperimentBean map(Experiment experiment);

    /**
     * Maps experiments entities list to experiments report beans.
     *
     * @param experiments - experiment entities list
     * @return experiments beans list
     */
    public abstract List<ExperimentBean> map(List<Experiment> experiments);

    /**
     * Maps experiments entities page to base report bean.
     *
     * @param experimentPage - experiments page
     * @return base report bean
     */
    @Mapping(source = "experimentPage.content", target = "items")
    @Mapping(source = "experimentPage.number", target = "page")
    @Mapping(source = "pageRequestDto.searchQuery", target = "searchQuery")
    public abstract BaseReportBean<ExperimentBean> map(Page<Experiment> experimentPage, PageRequestDto pageRequestDto);

    @Named("toFileName")
    protected String toFileName(String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        } else {
            return FilenameUtils.getName(path);
        }
    }

    @Named("formatLocalDateTime")
    protected String formatLocalDateTime(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime).map(dateTimeFormatter::format).orElse(null);
    }
}
