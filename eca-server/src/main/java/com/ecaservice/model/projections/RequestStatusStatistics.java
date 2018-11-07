package com.ecaservice.model.projections;

import com.ecaservice.model.entity.RequestStatus;

/**
 * Request statuses count statistics projection interface.
 *
 * @author Roman Batygin
 */
public interface RequestStatusStatistics {

    /**
     * Gets request status.
     *
     * @return request status
     */
    RequestStatus getRequestStatus();

    /**
     * Gets requests count with specified status.
     *
     * @return requests count with specified status
     */
    long getRequestsCount();
}
