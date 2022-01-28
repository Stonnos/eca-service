package com.ecaservice.server.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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
                                                 Function<List<Long>, List<T>> nextPageFunction,
                                                 Consumer<List<T>> pageContentAction,
                                                 int pageSize) {
        for (int offset = 0; offset < ids.size(); offset += pageSize) {
            var nextIds = ids.stream()
                    .skip(offset)
                    .limit(pageSize)
                    .collect(Collectors.toList());
            var nextPage = nextPageFunction.apply(nextIds);
            if (CollectionUtils.isEmpty(nextPage)) {
                log.debug("No one requests has been fetched");
                break;
            } else {
                pageContentAction.accept(nextPage);
            }
        }
    }
}
