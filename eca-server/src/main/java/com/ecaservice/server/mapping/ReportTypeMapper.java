package com.ecaservice.server.mapping;

import com.ecaservice.report.model.ReportType;
import com.ecaservice.web.dto.model.BaseReportType;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;

/**
 * Report type mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface ReportTypeMapper {

    /**
     * Maps base report type to report typ enum.
     *
     * @param baseReportType - base report type
     * @return report type enum
     */
    @ValueMapping(source = "EXPERIMENTS", target = "EXPERIMENTS")
    @ValueMapping(source = "EVALUATION_LOGS", target = "EVALUATION_LOGS")
    @ValueMapping(source = "CLASSIFIERS_OPTIONS_REQUESTS", target = "CLASSIFIERS_OPTIONS_REQUESTS")
    ReportType map(BaseReportType baseReportType);
}
