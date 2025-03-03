package com.ecaservice.server.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.exception.ClassifyInstanceInvalidRequestException;
import com.ecaservice.server.model.data.AttributeMetaInfo;
import com.ecaservice.server.model.data.AttributeType;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.repository.AttributesInfoRepository;
import com.ecaservice.web.dto.model.ClassifyInstanceRequestDto;
import com.google.common.math.DoubleMath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Classify instance service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifyInstanceValidator {

    private final AttributesInfoRepository attributesInfoRepository;

    /**
     * Validates classify instance request.
     *
     * @param classifyInstanceRequestDto - classify instance request
     * @param instancesInfo              - instances info
     */
    public void validate(ClassifyInstanceRequestDto classifyInstanceRequestDto, InstancesInfo instancesInfo) {
        var attributesInfo = attributesInfoRepository.findByInstancesInfo(instancesInfo)
                .orElseThrow(() -> new EntityNotFoundException(InstancesInfo.class, instancesInfo.getId()));
        var attributes = attributesInfo.getAttributes();
        int inputAttributesSize = attributes.size() - 1;
        if (inputAttributesSize != classifyInstanceRequestDto.getValues().size()) {
            throw new ClassifyInstanceInvalidRequestException(
                    String.format("Invalid attribute size [%d]. Expected [%d] input attributes",
                            classifyInstanceRequestDto.getValues().size(), inputAttributesSize));
        }
        var valuesMap = createValuesMap(classifyInstanceRequestDto);
        for (int i = 0; i < attributes.size(); i++) {
            AttributeMetaInfo attributeMetaInfo = attributes.get(i);
            // Check that class attribute not exists in input vector
            if (attributeMetaInfo.getName().equals(instancesInfo.getClassName()) && valuesMap.containsKey(i)) {
                String errorMessage =
                        String.format("Unexpected class attribute [%d] value [%s]. Expected only input attributes", i,
                                attributeMetaInfo.getName());
                throw new ClassifyInstanceInvalidRequestException(errorMessage);
            }
            if (!attributeMetaInfo.getName().equals(instancesInfo.getClassName())) {
                BigDecimal value = valuesMap.get(i);
                // Checks for valid nominal attribute code
                if (AttributeType.NOMINAL.equals(attributeMetaInfo.getType()) &&
                        !isValidNominalCode(value.doubleValue(), attributeMetaInfo)) {
                    String errorMessage = String.format(
                            "Invalid nominal value [%s] for attribute [%s]. Value must be integer value in interval [%d, %d]",
                            value, attributeMetaInfo.getName(), 0, attributeMetaInfo.getValues().size() - 1);
                    throw new ClassifyInstanceInvalidRequestException(errorMessage);
                }
            }
        }
    }

    private Map<Integer, BigDecimal> createValuesMap(ClassifyInstanceRequestDto classifyInstanceRequestDto) {
        Map<Integer, BigDecimal> valuesMap = newHashMap();
        for (var classifyInstanceValueDto : classifyInstanceRequestDto.getValues()) {
            if (valuesMap.containsKey(classifyInstanceValueDto.getIndex())) {
                throw new ClassifyInstanceInvalidRequestException(
                        String.format("Got duplicate attribute index [%d]", classifyInstanceValueDto.getIndex()));
            }
            valuesMap.put(classifyInstanceValueDto.getIndex(), classifyInstanceValueDto.getValue());
        }
        return valuesMap;
    }

    private boolean isValidNominalCode(Double value, AttributeMetaInfo attributeMetaInfo) {
        return DoubleMath.isMathematicalInteger(value) && value >= 0 && value < attributeMetaInfo.getValues().size();
    }
}
