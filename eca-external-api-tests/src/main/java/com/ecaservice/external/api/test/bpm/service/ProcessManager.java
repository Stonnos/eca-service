package com.ecaservice.external.api.test.bpm.service;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
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
     * @param versionTag         - process version tag
     * @param processBusinessKey - process business key
     * @param variables          - input variables
     */
    void startProcess(@NotBlank String processId, @NotBlank String versionTag, @NotBlank String processBusinessKey,
                      Map<String, Object> variables);
}
