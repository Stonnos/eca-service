package com.ecaservice.data.storage.model.report;

/**
 * Report type visitor interface.
 *
 * @param <T> - data type
 * @author Roman Batygin
 */
public interface ReportTypeVisitor<T> {

    /**
     * Method executes in case in report type is XLS.
     *
     * @return result data
     */
    T visitXls();

    /**
     * Method executes in case in report type is CSV.
     *
     * @return result data
     */
    T visitCsv();

    /**
     * Method executes in case in report type is ARFF.
     *
     * @return result data
     */
    T visitArff();

    /**
     * Method executes in case in report type is JSON.
     *
     * @return result data
     */
    T visitJson();

    /**
     * Method executes in case in report type is XML.
     *
     * @return result data
     */
    T visitXml();

    /**
     * Method executes in case in report type is TXT.
     *
     * @return result data
     */
    T visitTxt();

    /**
     * Method executes in case in report type is DATA.
     *
     * @return result data
     */
    T visitData();

    /**
     * Method executes in case in report type is DOCX.
     *
     * @return result data
     */
    T visitDocx();
}
