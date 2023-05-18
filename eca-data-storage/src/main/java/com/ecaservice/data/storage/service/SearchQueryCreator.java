package com.ecaservice.data.storage.service;

import com.ecaservice.core.filter.exception.FieldNotFoundException;
import com.ecaservice.data.storage.entity.AttributeType;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.model.AttributeInfo;
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
    private static final String LIKE_OR_PART = " lower(%s) like ? or";
    private static final String LIKE_PART = " lower(%s) like ?";
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

    private final AttributeService attributeService;

    /**
     * Builds sql search query based on page request model.
     *
     * @param instancesEntity - table name
     * @param pageRequestDto  - page request
     * @return sql prepared query
     */
    public SqlPreparedQuery buildSqlQuery(InstancesEntity instancesEntity, PageRequestDto pageRequestDto) {
        if (!isValidSortField(instancesEntity, pageRequestDto)) {
            throw new FieldNotFoundException(
                    String.format("Sort field [%s] doesn't exists", pageRequestDto.getSortField()));
        }
        return internalBuildSqlQuery(instancesEntity, pageRequestDto);
    }

    private SqlPreparedQuery internalBuildSqlQuery(InstancesEntity instancesEntity, PageRequestDto pageRequestDto) {
        var sqlPreparedQueryBuilder = SqlPreparedQuery.builder();
        StringBuilder queryString = new StringBuilder();
        if (StringUtils.isNotBlank(pageRequestDto.getSearchQuery())) {
            appendSearchQuery(instancesEntity, pageRequestDto.getSearchQuery().toLowerCase(), sqlPreparedQueryBuilder, queryString);
        }
        String sqlCountQuery = String.format(COUNT_QUERY_PART, instancesEntity.getTableName(), queryString);
        if (StringUtils.isNotBlank(pageRequestDto.getSortField())) {
            appendOrderBy(queryString, pageRequestDto);
        }
        appendLimitOffset(queryString, pageRequestDto);
        String sqlQuery = String.format(SELECT_PART, instancesEntity.getTableName(), queryString);
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

    private void appendSearchQuery(InstancesEntity instancesEntity,
                                   String searchQuery,
                                   SqlPreparedQuery.SqlPreparedQueryBuilder sqlPreparedQueryBuilder,
                                   StringBuilder queryString) {
        var tableSearchColumns = getTableSearchColumns(instancesEntity, searchQuery);
        Object[] args = new Object[tableSearchColumns.size()];
        queryString.append(WHERE_PART);
        int lastColumnIndex = tableSearchColumns.size() - 1;
        IntStream.range(0, lastColumnIndex).forEach(i -> {
            var attributeEntity = tableSearchColumns.get(i);
            appendSearchPredicate(attributeEntity, searchQuery, queryString, args, i, false);
        });
        var lastColumn = tableSearchColumns.get(lastColumnIndex);
        appendSearchPredicate(lastColumn, searchQuery, queryString, args, lastColumnIndex, true);
        sqlPreparedQueryBuilder.args(args);
    }

    private List<AttributeInfo> getTableSearchColumns(InstancesEntity instancesEntity, String searchQuery) {
        return attributeService.getsAttributesInfo(instancesEntity)
                .stream()
                .filter(attributeEntity -> isSearchSupported(attributeEntity, searchQuery))
                .collect(Collectors.toList());
    }

    private void appendSearchPredicate(AttributeInfo attributeInfo, String searchQuery,
                                       StringBuilder queryString, Object[] args, int argIndex, boolean lastPredicate) {
        if (isNumberSearchSupported(attributeInfo, searchQuery)) {
            String part = lastPredicate ? EQUAL_PART : EQUAL_OR_PART;
            queryString.append(String.format(part, attributeInfo.getColumnName()));
            args[argIndex] = new BigDecimal(searchQuery);
        } else if (AttributeType.NOMINAL.equals(attributeInfo.getType())) {
            String part = lastPredicate ? LIKE_PART : LIKE_OR_PART;
            queryString.append(String.format(part, attributeInfo.getColumnName()));
            args[argIndex] = MessageFormat.format(LIKE_FORMAT, searchQuery);
        } else {
            throw new IllegalStateException(
                    String.format("Can't create search predicate for attribute [%s] of type [%s]",
                            attributeInfo.getColumnName(), attributeInfo.getType()));
        }
    }

    private boolean isSearchSupported(AttributeInfo attributeInfo, String searchQuery) {
        return isNumberSearchSupported(attributeInfo, searchQuery) ||
                AttributeType.NOMINAL.equals(attributeInfo.getType());
    }

    private boolean isNumberSearchSupported(AttributeInfo attributeInfo, String searchQuery) {
        return AttributeType.NUMERIC.equals(attributeInfo.getType()) && NumberUtils.isParsable(searchQuery);
    }

    private boolean isValidSortField(InstancesEntity instancesEntity, PageRequestDto pageRequestDto) {
        if (StringUtils.isBlank(pageRequestDto.getSortField())) {
            return true;
        }
        var attributes = attributeService.getsAttributesInfo(instancesEntity);
        return attributes.stream()
                .anyMatch(attributeEntity -> attributeEntity.getColumnName().equals(pageRequestDto.getSortField()));
    }
}
