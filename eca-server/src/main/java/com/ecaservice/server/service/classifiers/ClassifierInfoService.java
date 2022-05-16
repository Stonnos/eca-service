package com.ecaservice.server.service.classifiers;

import com.ecaservice.classifier.options.model.AbstractHeterogeneousClassifierOptions;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.classifier.options.model.StackingOptions;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.server.mapping.ClassifierInfoMapper;
import com.ecaservice.server.model.entity.ClassifierInfo;
import com.ecaservice.server.service.classifiers.ClassifiersTemplateService;
import com.ecaservice.web.dto.model.ClassifierInfoDto;
import com.ecaservice.web.dto.model.FieldDictionaryDto;
import com.ecaservice.web.dto.model.FieldDictionaryValueDto;
import com.ecaservice.web.dto.model.FieldType;
import com.ecaservice.web.dto.model.FilterDictionaryValueDto;
import com.ecaservice.web.dto.model.FormFieldDto;
import com.ecaservice.web.dto.model.FormTemplateDto;
import com.ecaservice.web.dto.model.InputOptionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ecaservice.common.web.util.BeanUtil.invokeGetter;
import static com.ecaservice.server.util.ClassifierOptionsHelper.isEnsembleClassifierOptions;
import static com.ecaservice.server.util.ClassifierOptionsHelper.parseOptions;
import static com.ecaservice.server.util.Utils.formatValue;

/**
 * Service for processing classifier info.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifierInfoService {

    private static final String CLASSIFIER_DICTIONARY_NAME = "classifier";

    private final ClassifierInfoMapper classifierInfoMapper;
    private final ClassifiersTemplateService classifiersTemplateService;
    private final FilterService filterService;

    /**
     * Processes classifier input options json string to classifier info.
     *
     * @param classifierInfo - classifier options
     * @return classifier info
     */
    public ClassifierInfoDto processClassifierInfo(ClassifierInfo classifierInfo) {
        log.debug("Starting to process classifier info [{}]", classifierInfo.getClassifierName());
        ClassifierInfoDto classifierInfoDto;
        if (StringUtils.isNotEmpty(classifierInfo.getClassifierOptions())) {
            var classifierOptions = parseOptions(classifierInfo.getClassifierOptions());
            classifierInfoDto = internalProcessClassifierInfo(classifierOptions);
        } else {
            //Returns classifiers options list (for old data)
            classifierInfoDto = classifierInfoMapper.map(classifierInfo);
        }
        var classifierName = getClassifierNameLabel(classifierInfo.getClassifierName());
        classifierInfoDto.setClassifierName(classifierName);
        return classifierInfoDto;
    }

    private ClassifierInfoDto internalProcessClassifierInfo(ClassifierOptions classifierOptions) {
        ClassifierInfoDto classifierInfoDto = new ClassifierInfoDto();
        var template = getTemplate(classifierOptions);
        classifierInfoDto.setClassifierName(template.getTemplateTitle());
        var inputOptions = processInputOptions(template, classifierOptions);
        classifierInfoDto.setInputOptions(inputOptions);
        customizeClassifierInfo(classifierInfoDto, classifierOptions);
        log.debug("Classifier options class [{}] has been processed with result: {}",
                classifierOptions.getClass().getSimpleName(), classifierInfoDto);
        return classifierInfoDto;
    }

    private void customizeClassifierInfo(ClassifierInfoDto classifierInfoDto, ClassifierOptions classifierOptions) {
        if (isEnsembleClassifierOptions(classifierOptions)) {
            if (classifierOptions instanceof AbstractHeterogeneousClassifierOptions) {
                //Populates heterogeneous ensemble individual classifiers options
                var heterogeneousClassifierOptions = (AbstractHeterogeneousClassifierOptions) classifierOptions;
                var individualClassifiers = processClassifiers(heterogeneousClassifierOptions.getClassifierOptions());
                classifierInfoDto.setIndividualClassifiers(individualClassifiers);
            } else if (classifierOptions instanceof StackingOptions) {
                //Populates stacking individual classifiers options
                var stackingOptions = (StackingOptions) classifierOptions;
                var individualClassifiers = processClassifiers(stackingOptions.getClassifierOptions());
                classifierInfoDto.setIndividualClassifiers(individualClassifiers);
                var metaClassifierInfo = internalProcessClassifierInfo(stackingOptions.getMetaClassifierOptions());
                classifierInfoDto.getIndividualClassifiers().add(metaClassifierInfo);
            }
        }
    }

    private FormTemplateDto getTemplate(ClassifierOptions classifierOptions) {
        if (isEnsembleClassifierOptions(classifierOptions)) {
            return classifiersTemplateService.getEnsembleClassifierTemplateByClass(
                    classifierOptions.getClass().getSimpleName());
        } else {
            return classifiersTemplateService.getClassifierTemplateByClass(
                    classifierOptions.getClass().getSimpleName());
        }
    }

    private List<InputOptionDto> processInputOptions(FormTemplateDto template, ClassifierOptions classifierOptions) {
        return template.getFields()
                .stream()
                .map(formFieldDto -> {
                    var optionValue = getValue(classifierOptions, formFieldDto);
                    if (optionValue == null) {
                        log.debug("Got null value for field [{}] of type [{}]. Skipped...", formFieldDto.getFieldName(),
                                classifierOptions.getClass().getSimpleName());
                        return null;
                    }
                    InputOptionDto inputOptionDto = new InputOptionDto();
                    inputOptionDto.setOptionName(formFieldDto.getDescription());
                    inputOptionDto.setOptionValue(optionValue);
                    return inputOptionDto;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private String getValue(ClassifierOptions classifierOptions, FormFieldDto formFieldDto) {
        var optionValue = invokeGetter(classifierOptions, formFieldDto.getFieldName());
        if (optionValue == null) {
            return null;
        }
        if (FieldType.REFERENCE.equals(formFieldDto.getFieldType()) && formFieldDto.getDictionary() != null) {
            log.debug("Gets field [{}] value from dictionary for code [{}]", formFieldDto.getFieldName(), optionValue);
            return getLabelFromDictionary(formFieldDto.getDictionary(), String.valueOf(optionValue));
        }
        return formatValue(optionValue);
    }

    private String getClassifierNameLabel(String classifierName) {
        var classifiersDictionary = filterService.getFilterDictionary(CLASSIFIER_DICTIONARY_NAME);
        return classifiersDictionary.getValues()
                .stream()
                .filter(fieldDictionaryValue -> fieldDictionaryValue.getValue().equals(classifierName))
                .map(FilterDictionaryValueDto::getLabel)
                .findFirst()
                .orElse(null);
    }

    private String getLabelFromDictionary(FieldDictionaryDto fieldDictionary, String code) {
        if (CollectionUtils.isEmpty(fieldDictionary.getValues())) {
            log.debug("Dictionary [{}] has empty values", fieldDictionary.getName());
            return null;
        }
        return fieldDictionary.getValues()
                .stream()
                .filter(fieldDictionaryValue -> fieldDictionaryValue.getValue().equals(code))
                .map(FieldDictionaryValueDto::getLabel)
                .findFirst()
                .orElse(null);
    }

    private List<ClassifierInfoDto> processClassifiers(List<ClassifierOptions> classifierOptions) {
        return classifierOptions.stream()
                .map(this::internalProcessClassifierInfo)
                .collect(Collectors.toList());
    }
}
