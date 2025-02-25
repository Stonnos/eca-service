package com.ecaservice.server.service;

import com.ecaservice.web.dto.model.RocCurveDataDto;
import com.ecaservice.web.dto.model.RocCurvePointDto;
import eca.core.evaluation.Evaluation;
import eca.roc.RocCurve;
import lombok.experimental.UtilityClass;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.core.Instances;
import weka.core.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Roc curve helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class RocCurveHelper {

    private static final int SCALE = 4;

    /**
     * Calculates roc curve data.
     *
     * @param evaluation - evaluation object
     * @param classIndex - class index
     * @return roc curve data dto
     */
    public static RocCurveDataDto calculateRocCurveData(Evaluation evaluation, int classIndex) {
        RocCurve rocCurve = new RocCurve(evaluation);
        Instances rocCurveData = rocCurve.getROCCurve(classIndex);
        var rocCurvePoints = rocCurveData.stream()
                .map(instance -> {
                    RocCurvePointDto rocCurveDataDto = new RocCurvePointDto();
                    double specificity = instance.value(RocCurve.SPECIFICITY_INDEX) * 100;
                    double sensitivity = instance.value(RocCurve.SENSITIVITY_INDEX) * 100;
                    rocCurveDataDto.setSpecificity(
                            BigDecimal.valueOf(specificity).setScale(SCALE, RoundingMode.HALF_UP));
                    rocCurveDataDto.setSensitivity(
                            BigDecimal.valueOf(sensitivity).setScale(SCALE, RoundingMode.HALF_UP));
                    return rocCurveDataDto;
                }).toList();
        RocCurveDataDto rocCurveDataDto = new RocCurveDataDto();
        rocCurveDataDto.setAucValue(getROCArea(rocCurveData));
        rocCurveDataDto.setRocCurvePoints(rocCurvePoints);
        return rocCurveDataDto;
    }

    private static BigDecimal getROCArea(Instances rocCurveData) {
        double aucValue = ThresholdCurve.getROCArea(rocCurveData);
        if (!Utils.isMissingValue(aucValue)) {
            return BigDecimal.valueOf(aucValue).setScale(SCALE, RoundingMode.HALF_UP);
        }
        return null;
    }
}
