package com.ecaservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Callback interface for paging.
 *
 * @param <T> - data type
 * @author Roman Batygin
 */
public interface PageableCallback<T> {

    /**
     * Performs action with specified data list.
     *
     * @param list processing data list
     */
    void perform(List<T> list);

    /**
     * Finds the next page.
     *
     * @param pageable {@link Pageable} object
     * @return {@link Page} object
     */
    Page<T> findNextPage(Pageable pageable);
}
