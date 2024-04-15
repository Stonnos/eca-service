package com.ecaservice.classifier.template.processor.service;

import com.ecaservice.classifier.options.model.AbstractHeterogeneousClassifierOptions;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.classifier.options.model.StackingOptions;
import com.ecaservice.classifier.template.processor.config.ClassifiersTemplateProperties;
import com.ecaservice.common.web.expression.SpelExpressionHelper;
import com.ecaservice.web.dto.model.ClassifierInfoDto;
import com.ecaservice.web.dto.model.FieldDictionaryDto;
import com.ecaservice.web.dto.model.FieldDictionaryValueDto;
import com.ecaservice.web.dto.model.FieldType;
import com.ecaservice.web.dto.model.FormFieldDto;
import com.ecaservice.web.dto.model.FormTemplateDto;
import com.ecaservice.web.dto.model.InputOptionDto;
import eca.text.NumericFormatFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ecaservice.classifier.template.processor.util.Utils.isEnsembleClassifierOptions;
import static com.ecaservice.classifier.template.processor.util.Utils.toJsonString;

/**
 * Service for processing classifier info.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifierOptionsProcessor {

    private static final double TEN = 10d;

    private static final DecimalFormat DEFAULT_DECIMAL_FORMAT = NumericFormatFactory.getInstance(Integer.MAX_VALUE);

    private final ClassifiersTemplateProvider classifiersTemplateProvider;
    private final ClassifiersTemplateProperties classifiersTemplateProperties;
    private final SpelExpressionHelper spelExpressionHelper = new SpelExpressionHelper();

    private DecimalFormat decimalFormat;

    /**
     * Initialization method.
     */
    @PostConstruct
    public void initialize() {
        decimalFormat = NumericFormatFactory.getInstance(classifiersTemplateProperties.getMaximumFractionDigits());
    }

    /**
     * Processes classifier input options json string to human readable format.
     *
     * @param classifierOptions - classifier options
     * @return classifier options list
     */
    public List<InputOptionDto> processInputOptions(ClassifierOptions classifierOptions) {
        log.debug("Starting to process classifier options json [{}]", classifierOptions);
        var template = getTemplate(classifierOptions);
        var inputOptions = processInputOptions(template, classifierOptions);
        log.debug("Classifier options json has been processed with result: {}", inputOptions);
        return inputOptions;
    }

    /**
     * Processes classifier options.
     *
     * @param classifierOptions - classifier options
     * @return classifier info dto
     */
    public ClassifierInfoDto processClassifierOptions(ClassifierOptions classifierOptions) {
        ClassifierInfoDto classifierInfoDto = new ClassifierInfoDto();
        var template = getTemplate(classifierOptions);
        String templateTitle = processTemplateTitle(template, classifierOptions);
        classifierInfoDto.setClassifierName(template.getObjectClass());
        classifierInfoDto.setClassifierDescription(templateTitle);
        classifierInfoDto.setClassifierOptionsJson(toJsonString(classifierOptions));
        var inputOptions = processInputOptions(template, classifierOptions);
        classifierInfoDto.setInputOptions(inputOptions);
        customizeClassifierInfo(classifierInfoDto, classifierOptions);
        log.debug("Classifier options class [{}] has been processed with result: {}",
                classifierOptions.getClass().getSimpleName(), classifierInfoDto);
        return classifierInfoDto;
    }

    /**
     * Processes template title.
     *
     * @param formTemplateDto   - form template dto
     * @param classifierOptions - classifier options
     * @return template title
     */
    public String processTemplateTitle(FormTemplateDto formTemplateDto, ClassifierOptions classifierOptions) {
        if (StringUtils.isNotEmpty(formTemplateDto.getTemplateTitleFieldRef())) {
            FormFieldDto formFieldDto = formTemplateDto.getFields()
                    .stream()
                    .filter(field -> field.getFieldName().equals(formTemplateDto.getTemplateTitleFieldRef()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            String.format("Can't find form template [%s] title field ref [%s]",
                                    formTemplateDto.getTemplateName(), formTemplateDto.getTemplateTitleFieldRef())));
            return getValue(classifierOptions, formFieldDto);
        } else {
            return formTemplateDto.getTemplateTitle();
        }
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
                var metaClassifierInfo = processClassifierOptions(stackingOptions.getMetaClassifierOptions());
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

    private String createNullSafeExpression(String fieldName) {
        return StringUtils.replace(fieldName, ".", "?.");
    }

    private String getValue(ClassifierOptions classifierOptions, FormFieldDto formFieldDto) {
        var expression = createNullSafeExpression(formFieldDto.getFieldName());
        var optionValue = spelExpressionHelper.parseExpression(classifierOptions, expression);
        if (optionValue == null) {
            return null;
        }
        if (FieldType.REFERENCE.equals(formFieldDto.getFieldType()) && formFieldDto.getDictionary() != null) {
            log.debug("Gets field [{}] value from dictionary for code [{}]", formFieldDto.getFieldName(), optionValue);
            return getLabelFromDictionary(formFieldDto.getDictionary(), String.valueOf(optionValue));
        }
        return formatValue(optionValue);
    }

    private String formatValue(Object value) {
        if (Objects.isNull(value)) {
            return null;
        }
        if (value instanceof Number) {
            var numberValue = (Number) value;
            double accuracy = Math.pow(TEN, -decimalFormat.getMaximumFractionDigits());
            if (numberValue.doubleValue() < accuracy) {
                // Not round number value if value < accuracy
                return DEFAULT_DECIMAL_FORMAT.format(numberValue);
            }
            return decimalFormat.format(numberValue);
        }
        return String.valueOf(value);
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
                .map(this::processClassifierOptions)
                .collect(Collectors.toList());
    }
}
