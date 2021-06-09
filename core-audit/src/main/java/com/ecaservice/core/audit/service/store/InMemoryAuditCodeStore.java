package com.ecaservice.core.audit.service.store;

import com.ecaservice.core.audit.entity.EventType;
import com.ecaservice.core.audit.model.AuditCodeModel;
import com.ecaservice.core.audit.model.AuditEventTemplateModel;
import com.ecaservice.core.audit.service.AuditCodeStore;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * In memory audit codes store implementation.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class InMemoryAuditCodeStore implements AuditCodeStore {

    /**
     * Loads audit codes in memory.
     *
     * @throws IOException in case of I/O error
     */
    @PostConstruct
    public void init() throws IOException {
        var resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource("");
        @Cleanup InputStream inputStream = resource.getInputStream();
    }

    @Override
    public AuditCodeModel getAuditCode(String code) {
        return null;
    }

    @Override
    public AuditEventTemplateModel getAuditEventTemplate(String auditCode, EventType eventType) {
        return null;
    }
}
