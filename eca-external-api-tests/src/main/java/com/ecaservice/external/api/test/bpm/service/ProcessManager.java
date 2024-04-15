package com.ecaservice.external.api.test.bpm.service;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Map;

/**
 * Interface for process management.
 *
 * @author Roman Batygin
 */
@Validated
public interface ProcessManager {

    /**
     * Starts process.
     *
     * @param processId          - process id
     * @param processBusinessKey - process business key
     * @param variables          - input variables
     */
    void startProcess(@NotBlank String processId, @NotBlank String processBusinessKey,
                      @NotEmpty Map<String, Object> variables);
}
