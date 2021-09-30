package com.ecaservice.server.service;

import com.ecaservice.web.dto.model.PageRequestDto;
import org.springframework.data.domain.Page;

/**
 * Page request service interface.
 *
 * @param <T> - page entity generic type
 * @author Roman Batygin
 */
public interface PageRequestService<T> {

    /**
     * Gets the next page for specified page request.
     *
     * @param pageRequestDto - page request
     * @return entities page
     */
    Page<T> getNextPage(PageRequestDto pageRequestDto);
}
