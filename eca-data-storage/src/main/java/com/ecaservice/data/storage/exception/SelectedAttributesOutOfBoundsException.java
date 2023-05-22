package com.ecaservice.data.storage.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.data.storage.dto.DsErrorCode;

import static com.ecaservice.data.storage.util.Utils.MIN_NUM_CLASSES;
import static com.ecaservice.data.storage.util.Utils.MIN_NUM_SELECTED_ATTRIBUTES;

/**
 * Exception throws in case if selected attributes number is too low.
 *
 * @author Roman Batygin
 */
public class SelectedAttributesOutOfBoundsException extends ValidationErrorException {

    /**
     * Creates exception object.
     *
     * @param id - instances id
     */
    public SelectedAttributesOutOfBoundsException(long id) {
        super(DsErrorCode.SELECTED_ATTRIBUTES_OUT_OF_BOUNDS.name(),
                String.format("Selected attributes number must be greater than or equal to [%d] for table [%s]",
                        id, MIN_NUM_SELECTED_ATTRIBUTES));
    }
}
