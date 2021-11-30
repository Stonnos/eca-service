package com.ecaservice.server.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Page helper utility class.
 *
 * @author Roman Batygin
 */
@Slf4j
@UtilityClass
public class PageHelper {

    /**
     * Processed ids using pagination.
     *
     * @param ids               - ids list
     * @param nextPageFunction  - next page function
     * @param pageContentAction - page processing consumer
     * @param pageSize          - page size
     * @param <T>               - entity generic type
     */
    public static <T> void processWithPagination(List<Long> ids,
                                                 BiFunction<List<Long>, Pageable, Page<T>> nextPageFunction,
                                                 Consumer<List<T>> pageContentAction,
                                                 int pageSize) {
        Pageable pageRequest = PageRequest.of(0, pageSize);
        Page<T> page;
        do {
            page = nextPageFunction.apply(ids, pageRequest);
            if (page == null || !page.hasContent()) {
                log.debug("No one requests has been fetched");
                break;
            } else {
                log.debug("Process page [{}] of [{}] with size [{}]", page.getNumber(), page.getTotalPages(),
                        page.getSize());
                pageContentAction.accept(page.getContent());
            }
            pageRequest = page.nextPageable();
        } while (page.hasNext());
    }
}
