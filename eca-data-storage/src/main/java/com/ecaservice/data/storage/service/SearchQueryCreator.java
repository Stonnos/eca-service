package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.model.SqlPreparedQuery;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.stream.IntStream;

/**
 * Implements search query creator.
 *
 * @author Roman Batygin
 */
@Service
@RequiredArgsConstructor
public class SearchQueryCreator {

    private static final String LIKE_FORMAT = "%{0}%";
    private static final String WHERE_PART = " where";
    private static final String LIKE_OR_PART = " %s like ? or";
    private static final String LIKE_PART = " %s like ?";
    private static final String LIMIT_OFFSET_PART = " limit %d offset %d";
    private static final String SELECT_PART = "select * from %s";
    private static final String ORDER_BY_PART = " order by %s %s";
    private static final String DESC = "desc";
    private static final String ASC = "asc";

    private final TableMetaDataProvider tableMetaDataProvider;

    /**
     * Builds sql search query based on page request model.
     *
     * @param tableName      - table name
     * @param pageRequestDto - page request
     * @return sql prepared query
     */
    public SqlPreparedQuery buildSqlQuery(String tableName, PageRequestDto pageRequestDto) {
        var sqlPreparedQueryBuilder = SqlPreparedQuery.builder();
        StringBuilder queryString = new StringBuilder(String.format(SELECT_PART, tableName));
        if (StringUtils.isNotBlank(pageRequestDto.getSearchQuery())) {
            appendSearchQuery(tableName, pageRequestDto.getSearchQuery(), sqlPreparedQueryBuilder, queryString);
        }
        if (StringUtils.isNotBlank(pageRequestDto.getSortField())) {
            String sortMode = pageRequestDto.isAscending() ? ASC : DESC;
            queryString.append(String.format(ORDER_BY_PART, pageRequestDto.getSortField(), sortMode));
        }
        int offset = pageRequestDto.getPage() * pageRequestDto.getSize();
        queryString.append(String.format(LIMIT_OFFSET_PART, pageRequestDto.getSize(), offset));
        return sqlPreparedQueryBuilder
                .query(queryString.toString())
                .build();
    }

    private void appendSearchQuery(String tableName,
                                   String searchQuery,
                                   SqlPreparedQuery.SqlPreparedQueryBuilder sqlPreparedQueryBuilder,
                                   StringBuilder queryString) {
        var columns = tableMetaDataProvider.getTableColumns(tableName);
        Object[] args = new Object[columns.size()];
        queryString.append(WHERE_PART);
        int lastColumnIndex = columns.size() - 1;
        IntStream.range(0, lastColumnIndex).forEach(i -> {
            var columnModel = columns.get(i);
            queryString.append(String.format(LIKE_OR_PART, columnModel.getColumnName()));
            args[i] = MessageFormat.format(LIKE_FORMAT, searchQuery);
        });
        var lastColumn = columns.get(lastColumnIndex);
        queryString.append(String.format(LIKE_PART, lastColumn.getColumnName()));
        args[lastColumnIndex] = MessageFormat.format(LIKE_FORMAT, searchQuery);
        sqlPreparedQueryBuilder.args(args);
    }
}
