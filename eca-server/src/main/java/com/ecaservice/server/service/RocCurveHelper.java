package com.ecaservice.server.service;

import com.ecaservice.web.dto.model.RocCurveDataDto;
import com.ecaservice.web.dto.model.RocCurvePointDto;
import eca.core.evaluation.Evaluation;
import eca.roc.RocCurve;
import eca.roc.ThresholdModel;
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
    private static final int HUNDRED = 100;

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
                    double specificity = instance.value(RocCurve.SPECIFICITY_INDEX) * HUNDRED;
                    double sensitivity = instance.value(RocCurve.SENSITIVITY_INDEX) * HUNDRED;
                    rocCurveDataDto.setSpecificity(
                            BigDecimal.valueOf(specificity).setScale(SCALE, RoundingMode.HALF_UP));
                    rocCurveDataDto.setSensitivity(
                            BigDecimal.valueOf(sensitivity).setScale(SCALE, RoundingMode.HALF_UP));
                    return rocCurveDataDto;
                }).toList();
        RocCurveDataDto rocCurveDataDto = new RocCurveDataDto();
        rocCurveDataDto.setAucValue(getROCArea(rocCurveData));
        rocCurveDataDto.setRocCurvePoints(rocCurvePoints);
        rocCurveDataDto.setOptimalPoint(calculateOptimalPoint(rocCurve, rocCurveData));
        return rocCurveDataDto;
    }

    private static RocCurvePointDto calculateOptimalPoint(RocCurve rocCurve, Instances rocCurveData) {
        ThresholdModel thresholdModel = rocCurve.findOptimalThreshold(rocCurveData);
        RocCurvePointDto rocCurvePointDto = new RocCurvePointDto();
        rocCurvePointDto.setSpecificity(
                BigDecimal.valueOf(thresholdModel.getSpecificity() * HUNDRED).setScale(SCALE, RoundingMode.HALF_UP));
        rocCurvePointDto.setSensitivity(
                BigDecimal.valueOf(thresholdModel.getSensitivity() * HUNDRED).setScale(SCALE, RoundingMode.HALF_UP));
        rocCurvePointDto.setThreshold(
                BigDecimal.valueOf(thresholdModel.getThresholdValue()).setScale(SCALE, RoundingMode.HALF_UP));
        return rocCurvePointDto;
    }

    private static BigDecimal getROCArea(Instances rocCurveData) {
        double aucValue = ThresholdCurve.getROCArea(rocCurveData);
        if (!Utils.isMissingValue(aucValue)) {
            return BigDecimal.valueOf(aucValue).setScale(SCALE, RoundingMode.HALF_UP);
        }
        return null;
    }
}
