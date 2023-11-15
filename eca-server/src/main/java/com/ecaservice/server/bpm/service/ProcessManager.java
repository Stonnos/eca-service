package com.ecaservice.server.bpm.service;

import java.util.Map;

/**
 * Interface for process management.
 *
 * @author Roman Batygin
 */
public interface ProcessManager {

    /**
     * Starts process.
     *
     * @param processId          - process id
     * @param processBusinessKey - process business key
     * @param variables          - input variables
     */
    void startProcess(String processId, String processBusinessKey, Map<String, Object> variables);

    /**
     * Checks active process existing with specified process business key.
     *
     * @param processBusinessKey - process business key
     * @return {@code true} if active process exists, otherwise {@code false}
     */
    boolean hasActiveProcess(String processBusinessKey);
}
