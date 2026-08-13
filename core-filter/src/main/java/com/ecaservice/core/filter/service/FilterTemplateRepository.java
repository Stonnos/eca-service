package com.ecaservice.core.filter.service;

import com.ecaservice.core.filter.model.FilterDictionary;
import com.ecaservice.core.filter.model.FilterTemplate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Filter template repository.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
@Getter
public class FilterTemplateRepository {

    private final List<FilterDictionary> dictionaries;
    private final List<FilterTemplate> templates;
}
