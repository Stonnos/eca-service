package com.ecaservice.server.service.classifiers;

import com.ecaservice.core.filter.validation.annotations.ValidPageRequest;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.report.model.ClassifiersConfigurationBean;
import com.ecaservice.web.dto.model.ClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.CreateClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.UpdateClassifiersConfigurationDto;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;

import static com.ecaservice.server.model.entity.FilterTemplateType.CLASSIFIERS_CONFIGURATION;

/**
 * Classifiers configuration interface.
 *
 * @author Roman Batygin
 */
@Validated
public interface ClassifiersConfigurationService {

    /**
     * Gets next classifiers configuration page.
     *
     * @param pageRequestDto - page request dto
     * @return classifiers configuration page
     */
    Page<ClassifiersConfiguration> getNextPage(PageRequestDto pageRequestDto);

    /**
     * Saves new classifiers configuration.
     *
     * @param configurationDto - create classifiers configuration dto
     * @return classifiers configuration entity
     */
    ClassifiersConfiguration save(CreateClassifiersConfigurationDto configurationDto);

    /**
     * Updates classifiers configuration.
     *
     * @param configurationDto - update classifiers configuration dto
     * @return classifiers configuration entity
     */
    ClassifiersConfiguration update(UpdateClassifiersConfigurationDto configurationDto);

    /**
     * Deletes classifiers configuration by specified id.
     *
     * @param id - classifiers configuration id
     */
    void delete(long id);

    /**
     * Creates classifiers configuration copy. Created copy will always be not build in and not active.
     *
     * @param configurationDto - configuration data
     * @return classifiers configuration copy entity
     */
    ClassifiersConfiguration copy(UpdateClassifiersConfigurationDto configurationDto);

    /**
     * Sets classifiers configuration as active.
     *
     * @param id - configuration id
     * @return active classifiers configuration entity
     */
    ClassifiersConfiguration setActive(long id);

    /**
     * Gets classifiers configurations dto models page
     *
     * @param pageRequestDto - page request object
     * @return classifiers configurations dto models page
     */
    PageDto<ClassifiersConfigurationDto> getClassifiersConfigurations(
            @ValidPageRequest(filterTemplateName = CLASSIFIERS_CONFIGURATION) PageRequestDto pageRequestDto);

    /**
     * Gets classifiers configuration details by id.
     *
     * @param id - configuration id
     * @return classifiers configuration dto
     */
    ClassifiersConfigurationDto getClassifiersConfigurationDetails(long id);

    /**
     * Gets classifiers configuration report by id.
     *
     * @param id - configuration id
     * @return classifiers configuration report
     */
    ClassifiersConfigurationBean getClassifiersConfigurationReport(long id);

    /**
     * Gets classifiers configuration by id.
     *
     * @param id - configuration by id
     * @return classifiers configuration entity
     */
    ClassifiersConfiguration getById(long id);
}
