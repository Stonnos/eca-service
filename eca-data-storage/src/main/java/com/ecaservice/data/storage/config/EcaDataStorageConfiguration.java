package com.ecaservice.data.storage.config;

import com.ecaservice.oauth2.annotation.Oauth2ResourceServer;
import eca.data.db.SqlQueryHelper;
import eca.data.db.SqlTypeUtils;
import eca.data.file.FileDataLoader;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;

/**
 * Eca data storage configuration.
 *
 * @author Roman Batygin
 */
@Configuration
@Oauth2ResourceServer
@EnableConfigurationProperties(EcaDsConfig.class)
public class EcaDataStorageConfiguration {

    /**
     * Creates sql query helper bean.
     *
     * @return sql query helper bean
     */
    @Bean
    public SqlQueryHelper sqlQueryHelper() {
        SqlQueryHelper sqlQueryHelper = new SqlQueryHelper();
        sqlQueryHelper.setDateColumnType(SqlTypeUtils.TIMESTAMP_TYPE);
        return sqlQueryHelper;
    }

    /**
     * Creates file data loader bean.
     *
     * @param ecaDsConfig - eca ds config
     * @return file data loader bean
     */
    @Bean
    @Scope(value = SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public FileDataLoader fileDataLoader(EcaDsConfig ecaDsConfig) {
        FileDataLoader dataLoader = new FileDataLoader();
        dataLoader.setDateFormat(ecaDsConfig.getDateFormat());
        return dataLoader;
    }
}