package com.ecaservice.base.model;

import com.ecaservice.base.model.visitor.EcaRequestVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Instances request dto.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstancesRequest implements EcaRequest {

    /**
     * Train data uuid
     */
    @NotBlank
    private String dataUuid;

    @Override
    public void visit(EcaRequestVisitor ecaRequestVisitor) {
        ecaRequestVisitor.visitInstancesRequest(this);
    }
}
