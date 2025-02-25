package com.ecaservice.server.util;

import com.ecaservice.server.exception.InvalidClassIndexException;
import com.ecaservice.server.exception.ModelDeletedException;
import com.ecaservice.server.exception.UnexpectedRequestStatusException;
import com.ecaservice.server.model.entity.AbstractEvaluationEntity;
import com.ecaservice.server.model.entity.RequestStatus;
import lombok.experimental.UtilityClass;

/**
 * Validation utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ValidationUtils {

    /**
     * Checks that class index is in interval [0, numClasses].
     *
     * @param classValueIndex - class value index
     * @param numClasses      - num classes
     */
    public static void checkClassIndex(int classValueIndex, int numClasses) {
        if (classValueIndex < 0 || classValueIndex >= numClasses) {
            String errorMessage =
                    String.format("Invalid class index [%d]. Must be in interval [%d, %d)", classValueIndex, 0,
                            numClasses);
            throw new InvalidClassIndexException(errorMessage);
        }
    }

    /**
     * Checks that model not deleted.
     *
     * @param evaluationEntity - evaluation entity
     */
    public static void checkModelNotDeleted(AbstractEvaluationEntity evaluationEntity) {
        if (evaluationEntity.getDeletedDate() != null) {
            throw new ModelDeletedException();
        }
    }

    /**
     * Checks that request status is finished.
     *
     * @param evaluationEntity - evaluation entity
     */
    public static void checkFinishedRequestStatus(AbstractEvaluationEntity evaluationEntity) {
        if (!RequestStatus.FINISHED.equals(evaluationEntity.getRequestStatus())) {
            String errorMessage = String.format("Request must be in [%s] status", RequestStatus.FINISHED);
            throw new UnexpectedRequestStatusException(errorMessage);
        }
    }
}
