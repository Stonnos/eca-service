package com.ecaservice;

import lombok.experimental.UtilityClass;

/**
 * Page request utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class PageRequestUtils {

    public static final String PAGE_PARAM = "page";
    public static final String SIZE_PARAM = "size";
    public static final String FILTER_NAME_PARAM = "filters[0].name";

    public static final int PAGE = 0;
    public static final int SIZE = 10;
}
