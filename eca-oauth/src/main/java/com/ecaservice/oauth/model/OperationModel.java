package com.ecaservice.oauth.model;

import com.ecaservice.oauth.model.openapi.Operation;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestMethod;

@Data
@Builder
public class OperationModel {

    private RequestMethod requestMethod;

    private Operation operation;
}
