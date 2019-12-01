package com.ecaservice.report;

import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.report.model.ExperimentBean;
import com.ecaservice.report.model.FilterBean;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author Roman Batygin
 */
public class A {

    public static void main(String[] args) throws Exception {
        BaseReportBean<ExperimentBean> beanBaseReportBean = new BaseReportBean<>();
        beanBaseReportBean.setPage(1);
        beanBaseReportBean.setTotalPages(22);
        beanBaseReportBean.setSearchQuery("  ");
        FilterBean filterBean = new FilterBean();
        filterBean.setDescription("Дата создания заявки");
        filterBean.setData("2018-23-34");
        FilterBean filterBean1 = new FilterBean();
        filterBean1.setDescription("Тип эксперимента");
        filterBean1.setData("Нейронные сети, Неоднородный ансамбль, Алгоритм KNN");
        beanBaseReportBean.setFilters(Arrays.asList(filterBean, filterBean1));
        //beanBaseReportBean.setFilters(Collections.emptyList());
        ExperimentBean experimentBean = new ExperimentBean();
        experimentBean.setCreationDate(LocalDateTime.now());
        experimentBean.setDeletedDate(LocalDateTime.now());
        experimentBean.setUuid(UUID.randomUUID().toString());
        experimentBean.setEvaluationMethod("V-блочная кросс проверка");
        experimentBean.setTrainingDataAbsolutePath("data_" + experimentBean.getUuid());
        experimentBean.setExperimentAbsolutePath("experiment_" + experimentBean.getUuid());
        ExperimentBean experimentBean1 = new ExperimentBean();
        experimentBean1.setUuid(UUID.randomUUID().toString());

        beanBaseReportBean.setItems(Arrays.asList(experimentBean, experimentBean1));
        BaseReportGenerator.generateExperimentsReport(beanBaseReportBean,
                new FileOutputStream("D:/results-report.xlsx"));
    }
}
