package com.ecaservice.core.audit.util;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.Cleanup;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Csv utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class CsvUtils {

    /**
     * Reads data from csv resource.
     *
     * @param inputStream    - resource input stream
     * @param columnMappings - column mappings in result java object
     * @param clazz          - model java class
     * @param <V>            - model type
     * @return result data
     */
    public static <V> List<V> readCsv(InputStream inputStream, String[] columnMappings, Class<V> clazz) {
        @Cleanup InputStreamReader reader = new InputStreamReader(inputStream);
        final ColumnPositionMappingStrategy<V> mappingStrategy = new ColumnPositionMappingStrategy<>();
        mappingStrategy.setType(clazz);
        mappingStrategy.setColumnMapping(columnMappings);
        CsvToBean<V> csvToBean = new CsvToBeanBuilder<V>(reader)
                .withMappingStrategy(mappingStrategy)
                .build();
        return csvToBean.parse();
    }
}
