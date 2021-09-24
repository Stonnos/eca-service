package com.ecaservice.data.storage.service;

import com.ecaservice.core.filter.exception.FieldNotFoundException;
import com.ecaservice.data.storage.model.ColumnModel;
import com.ecaservice.data.storage.model.SqlPreparedQuery;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;
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
    private static final String SELECT_PART = "select * from %s%s";
    private static final String ORDER_BY_PART = " order by %s %s";
    private static final String EQUAL_PART = " %s = ?";
    private static final String EQUAL_OR_PART = " %s = ? or";
    private static final String DESC = "desc";
    private static final String ASC = "asc";
    private static final String NUMERIC_TYPE = "numeric";
    private static final String VARCHAR_TYPE = "character varying";
    private static final String COUNT_QUERY_PART = "select count(*) from %s%s";

    private final TableMetaDataProvider tableMetaDataProvider;

    /**
     * Builds sql search query based on page request model.
     *
     * @param tableName      - table name
     * @param pageRequestDto - page request
     * @return sql prepared query
     */
    public SqlPreparedQuery buildSqlQuery(String tableName, PageRequestDto pageRequestDto) {
        if (!isValidSortField(tableName, pageRequestDto)) {
            throw new FieldNotFoundException(
                    String.format("Sort field [%s] doesn't exists", pageRequestDto.getSortField()));
        }
        return internalBuildSqlQuery(tableName, pageRequestDto);
    }

    private SqlPreparedQuery internalBuildSqlQuery(String tableName, PageRequestDto pageRequestDto) {
        var sqlPreparedQueryBuilder = SqlPreparedQuery.builder();
        StringBuilder queryString = new StringBuilder();
        if (StringUtils.isNotBlank(pageRequestDto.getSearchQuery())) {
            appendSearchQuery(tableName, pageRequestDto.getSearchQuery(), sqlPreparedQueryBuilder, queryString);
        }
        String sqlCountQuery = String.format(COUNT_QUERY_PART, tableName, queryString.toString());
        if (StringUtils.isNotBlank(pageRequestDto.getSortField())) {
            appendOrderBy(queryString, pageRequestDto);
        }
        appendLimitOffset(queryString, pageRequestDto);
        String sqlQuery = String.format(SELECT_PART, tableName, queryString.toString());
        return sqlPreparedQueryBuilder
                .query(sqlQuery)
                .countQuery(sqlCountQuery)
                .build();
    }

    private void appendLimitOffset(StringBuilder queryString, PageRequestDto pageRequestDto) {
        int offset = pageRequestDto.getPage() * pageRequestDto.getSize();
        queryString.append(String.format(LIMIT_OFFSET_PART, pageRequestDto.getSize(), offset));
    }

    private void appendOrderBy(StringBuilder queryString, PageRequestDto pageRequestDto) {
        String sortMode = pageRequestDto.isAscending() ? ASC : DESC;
        queryString.append(String.format(ORDER_BY_PART, pageRequestDto.getSortField(), sortMode));
    }

    private void appendSearchQuery(String tableName,
                                   String searchQuery,
                                   SqlPreparedQuery.SqlPreparedQueryBuilder sqlPreparedQueryBuilder,
                                   StringBuilder queryString) {
        var columns = getTableSearchColumns(tableName, searchQuery);
        Object[] args = new Object[columns.size()];
        queryString.append(WHERE_PART);
        int lastColumnIndex = columns.size() - 1;
        IntStream.range(0, lastColumnIndex).forEach(i -> {
            var columnModel = columns.get(i);
            appendSearchPredicate(columnModel, searchQuery, queryString, args, i, false);
        });
        var lastColumn = columns.get(lastColumnIndex);
        appendSearchPredicate(lastColumn, searchQuery, queryString, args, lastColumnIndex, true);
        sqlPreparedQueryBuilder.args(args);
    }

    private List<ColumnModel> getTableSearchColumns(String tableName, String searchQuery) {
        return tableMetaDataProvider.getTableColumns(tableName).stream()
                .filter(columnModel -> isSearchSupported(columnModel, searchQuery))
                .collect(Collectors.toList());
    }

    private void appendSearchPredicate(ColumnModel columnModel, String searchQuery,
                                       StringBuilder queryString, Object[] args, int argIndex, boolean lastPredicate) {
        if (isNumberSearchSupported(columnModel, searchQuery)) {
            String part = lastPredicate ? EQUAL_PART : EQUAL_OR_PART;
            queryString.append(String.format(part, columnModel.getColumnName()));
            args[argIndex] = new BigDecimal(searchQuery);
        } else if (VARCHAR_TYPE.equals(columnModel.getDataType())) {
            String part = lastPredicate ? LIKE_PART : LIKE_OR_PART;
            queryString.append(String.format(part, columnModel.getColumnName()));
            args[argIndex] = MessageFormat.format(LIKE_FORMAT, searchQuery);
        } else {
            throw new IllegalStateException(String.format("Can't create search predicate for column [%s] of type [%s]",
                    columnModel.getColumnName(), columnModel.getDataType()));
        }
    }

    private boolean isSearchSupported(ColumnModel columnModel, String searchQuery) {
        return isNumberSearchSupported(columnModel, searchQuery) || VARCHAR_TYPE.equals(columnModel.getDataType());
    }

    private boolean isNumberSearchSupported(ColumnModel columnModel, String searchQuery) {
        return NUMERIC_TYPE.equals(columnModel.getDataType()) && NumberUtils.isParsable(searchQuery);
    }

    private boolean isValidSortField(String tableName, PageRequestDto pageRequestDto) {
        if (StringUtils.isBlank(pageRequestDto.getSortField())) {
            return true;
        }
        var columnNames = tableMetaDataProvider.getTableColumns(tableName).stream()
                .map(ColumnModel::getColumnName)
                .collect(Collectors.toList());
        return columnNames.contains(pageRequestDto.getSortField());
    }
}
