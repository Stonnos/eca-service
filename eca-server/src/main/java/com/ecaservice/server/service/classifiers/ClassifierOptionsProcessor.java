package com.ecaservice.server.service.classifiers;

import com.ecaservice.classifier.options.model.AbstractHeterogeneousClassifierOptions;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.classifier.options.model.StackingOptions;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.server.mapping.ClassifierInfoMapper;
import com.ecaservice.server.model.entity.ClassifierInfo;
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
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import javax.validation.Path;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ecaservice.server.service.filter.dictionary.FilterDictionaries.CLASSIFIER_NAME;
import static com.ecaservice.server.util.ClassifierOptionsHelper.isEnsembleClassifierOptions;
import static com.ecaservice.server.util.ClassifierOptionsHelper.parseOptions;
import static com.ecaservice.server.util.Utils.formatValue;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Service for processing classifier info.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifierOptionsProcessor {

    private static final String METHOD_KEY_FORMAT = "%s#%s";

    private final ClassifierInfoMapper classifierInfoMapper;
    private final ClassifiersTemplateProvider classifiersTemplateProvider;
    private final FilterService filterService;

    private Map<String, Method> gettersCache = newHashMap();

    /**
     * Initializes cache.
     */
    @PostConstruct
    public void initializeCache() {
        classifiersTemplateProvider.getClassifiersTemplates().forEach(this::cachePutTemplate);
        classifiersTemplateProvider.getEnsembleClassifiersTemplates().forEach(this::cachePutTemplate);
    }

    /**
     * Processes classifier input options json string to human readable format.
     *
     * @param classifierOptionsJson - classifier options json
     * @return classifier options list
     */
    public List<InputOptionDto> processInputOptions(String classifierOptionsJson) {
        log.debug("Starting to process classifier options json [{}]", classifierOptionsJson);
        var classifierOptions = parseOptions(classifierOptionsJson);
        var template = getTemplate(classifierOptions);
        var inputOptions = processInputOptions(template, classifierOptions);
        log.debug("Classifier options json has been processed with result: {}", inputOptions);
        return inputOptions;
    }

    /**
     * Processes classifier info to classifier info dto.
     *
     * @param classifierInfo - classifier info
     * @return classifier info dto
     */
    public ClassifierInfoDto processClassifierInfo(ClassifierInfo classifierInfo) {
        log.debug("Starting to process classifier info [{}]", classifierInfo.getClassifierName());
        ClassifierInfoDto classifierInfoDto;
        if (StringUtils.isNotEmpty(classifierInfo.getClassifierOptions())) {
            var classifierOptions = parseOptions(classifierInfo.getClassifierOptions());
            classifierInfoDto = processClassifierInfo(classifierOptions);
            classifierInfoDto.setClassifierName(classifierInfo.getClassifierName());
            classifierInfoDto.setClassifierOptionsJson(classifierInfo.getClassifierOptions());
        } else {
            //Returns classifiers options list (for old data)
            classifierInfoDto = classifierInfoMapper.map(classifierInfo);
        }
        var classifierName = getClassifierNameLabel(classifierInfo.getClassifierName());
        classifierInfoDto.setClassifierDescription(classifierName);
        return classifierInfoDto;
    }

    /**
     * Processes classifier input options to classifier info dto.
     *
     * @param classifierOptions - classifier options
     * @return classifier info dto
     */
    public ClassifierInfoDto processClassifierInfo(ClassifierOptions classifierOptions) {
        ClassifierInfoDto classifierInfoDto = new ClassifierInfoDto();
        var template = getTemplate(classifierOptions);
        classifierInfoDto.setClassifierName(template.getObjectClass());
        classifierInfoDto.setClassifierDescription(template.getTemplateTitle());
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
                var metaClassifierInfo = processClassifierInfo(stackingOptions.getMetaClassifierOptions());
                metaClassifierInfo.setMetaClassifier(true);
                classifierInfoDto.getIndividualClassifiers().add(metaClassifierInfo);
            }
        }
    }

    private FormTemplateDto getTemplate(ClassifierOptions classifierOptions) {
        if (isEnsembleClassifierOptions(classifierOptions)) {
            return classifiersTemplateProvider.getEnsembleClassifierTemplateByClass(
                    classifierOptions.getClass().getSimpleName());
        } else {
            return classifiersTemplateProvider.getClassifierTemplateByClass(
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
        var classifiersDictionary = filterService.getFilterDictionary(CLASSIFIER_NAME);
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
                .map(this::processClassifierInfo)
                .collect(Collectors.toList());
    }

    private Object invokeGetter(Object bean, String propertyName) {
        Path path = PathImpl.createPathFromString(propertyName);
        Object methodResult = bean;
        for (var node : path) {
            String key = String.format(METHOD_KEY_FORMAT, methodResult.getClass().getSimpleName(), node.getName());
            Method readMethod = gettersCache.get(key);
            Assert.notNull(readMethod, String.format("Method [%s] not found", key));
            methodResult = ReflectionUtils.invokeMethod(readMethod, methodResult);
            if (methodResult == null) {
                return null;
            }
        }
        return methodResult;
    }

    private void cachePutTemplate(FormTemplateDto templateDto) {
        log.info("Starting to initialize classifiers templates [{}] cache", templateDto.getTemplateName());
        templateDto.getFields().forEach(formFieldDto -> {
            Path path = PathImpl.createPathFromString(formFieldDto.getFieldName());
            try {
                Class<?> currentClazz = Class.forName(
                        String.format("%s.%s", ClassifierOptions.class.getPackageName(), templateDto.getObjectClass()));
                for (var node : path) {
                    var propertyDescriptor = BeanUtils.getPropertyDescriptor(currentClazz, node.getName());
                    var readMethod = propertyDescriptor.getReadMethod();
                    String key = String.format(METHOD_KEY_FORMAT, currentClazz.getSimpleName(), node.getName());
                    gettersCache.putIfAbsent(key, readMethod);
                    currentClazz = propertyDescriptor.getPropertyType();
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e.getMessage());
            }
        });
        log.info("Classifiers templates [{}] cache has been initialized", templateDto.getTemplateName());
    }
}
