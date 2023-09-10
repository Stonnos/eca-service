package com.ecaservice.oauth.exception;

import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.oauth.error.EcaOauthErrorCode;
import com.ecaservice.oauth.model.RuleResultDetails;
import lombok.Getter;

import java.util.List;

/**
 * Not safe password exception.
 *
 * @author Roman Batygin
 */
public class NotSafePasswordException extends ValidationErrorException {

    @Getter
    private final List<RuleResultDetails> details;

    /**
     * Creates exception object.
     *
     * @param details - validation rules result details
     * @param message - error message
     */
    public NotSafePasswordException(List<RuleResultDetails> details, String message) {
        super(EcaOauthErrorCode.NOT_SAFE_PASSWORD, message);
        this.details = details;
    }
}
