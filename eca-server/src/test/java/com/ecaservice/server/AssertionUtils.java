package com.ecaservice.server;

import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.Assertions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Assertion utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class AssertionUtils {

    /**
     * Asserts that list has only one element.
     *
     * @param list list object
     * @param <T>  generic type
     */
    public static <T> void hasOneElement(List<T> list) {
        Assertions.assertThat(list).hasSize(1);
    }

    /**
     * Asserts base report bean.
     *
     * @param baseReportBean - base report bean
     * @param pageRequestDto - page request dto
     * @param <T>            - beans generic type
     */
    public static <T> void assertBaseReportBean(BaseReportBean<T> baseReportBean, PageRequestDto pageRequestDto) {
        assertThat(baseReportBean).isNotNull();
        assertThat(baseReportBean.getPage()).isOne();
        assertThat(baseReportBean.getTotalPages()).isOne();
        assertThat(baseReportBean.getSearchQuery()).isNotNull();
        assertThat(baseReportBean.getItems()).isNotNull();
        assertThat(baseReportBean.getItems().size()).isOne();
        assertThat(baseReportBean.getFilters()).hasSameSizeAs(pageRequestDto.getFilters());
    }
}
