package com.ecaservice.oauth.model;

import com.ecaservice.oauth.model.openapi.Operation;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class OperationReport {

    private RequestMethod requestMethod;

    private Operation operation;
}
