package com.ecaservice.core.audit.config.freemarker;

import com.ecaservice.audit.dto.EventType;
import com.ecaservice.core.audit.service.AuditEventTemplateStore;
import freemarker.cache.TemplateLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.Reader;
import java.io.StringReader;

/**
 * Implements audit event template loading from database.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseTemplateLoader implements TemplateLoader {

    private static final int KEYS_LENGTH = 2;
    private static final String KEYS_SEPARATOR = "#";
    private static final int CODE_IDX = 0;
    private static final int EVENT_TYPE_IDX = 1;

    private final AuditEventTemplateStore auditEventTemplateStore;

    @Override
    public Object findTemplateSource(String code) {
        log.debug("Load audit event template [{}] from database", code);
        var keys = StringUtils.split(code, KEYS_SEPARATOR);
        Assert.state(keys.length == KEYS_LENGTH,
                String.format("Expected [%d] keys for audit template code [%s]", KEYS_LENGTH, code));
        var auditEventTemplate =
                auditEventTemplateStore.getAuditEventTemplate(keys[CODE_IDX], EventType.valueOf(keys[EVENT_TYPE_IDX]));
        log.debug("Audit event template [{}] has been loaded", code);
        return auditEventTemplate.getMessageTemplate();
    }

    @Override
    public long getLastModified(Object template) {
        return -1L;
    }

    @Override
    public Reader getReader(Object template, String encoding) {
        return new StringReader(String.valueOf(template));
    }

    @Override
    public void closeTemplateSource(Object template) {
        //Empty implementation
    }
}
