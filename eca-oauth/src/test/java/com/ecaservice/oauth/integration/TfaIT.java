package com.ecaservice.oauth.integration;

import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.mapping.Config2;
import com.ecaservice.oauth.mapping.OpenApiMapperImpl;
import com.ecaservice.oauth.mapping.OpenApiService;
import com.ecaservice.oauth.model.OpenApiModel;
import com.ecaservice.oauth.model.openapi.OpenAPI;
import com.ecaservice.oauth.service.mail.dictionary.TemplateVariablesDictionary;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import com.ecaservice.oauth2.test.token.TokenResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.Cleanup;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

/**
 * Integration tests for two-factor authentication functionality.
 *
 * @author Roman Batygin
 */
@Import({FreemarkerConfiguration.class, Config2.class, OpenApiMapperImpl.class, OpenApiService.class})
class TfaIT extends AbstractUserIT {

    private static final String TFA_CODE_PARAM = "tfa_code";
    private static final String TFA_CODE_GRANT_TYPE = "tfa_code";

    @Inject
    private Configuration configuration;
    @Inject
    private OpenApiService openApiService;

    @Override
    String getApiPrefix() {
        return StringUtils.EMPTY;
    }

    @Override
    void init() {
        super.init();
        //Enable tfa for user
        UserEntity userEntity = getUserEntity();
        userEntity.setTfaEnabled(true);
        getUserEntityRepository().save(userEntity);
    }

    @Test
    void testTfa() {
        login();
        String tfaCode = getVariableFromEmail(Templates.TFA_CODE, TemplateVariablesDictionary.TFA_CODE);
        confirmTfaCode(tfaCode);
    }

    @Test
    void t() throws IOException, TemplateException {
        WebClient webClient = WebClient.builder().baseUrl(String.format("http://localhost:%d", port)).build();
        assertThat(webClient).isNotNull();

        Mono<String> result = webClient.get()
                .uri("/v3/api-docs/")
                .retrieve()
                .bodyToMono(String.class);
        assertThat(result).isNotNull();
       // String json = result.block();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        @Cleanup InputStream inputStream = resolver.getResource("oa.json").getInputStream();
        @Cleanup Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        String json = FileCopyUtils.copyToString(reader);
        assertThat(json).isNotBlank();

        final var objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        OpenAPI openAPI = objectMapper.readValue(json, OpenAPI.class);
        System.out.println();

        var template = configuration.getTemplate("classpath:templates/template.adoc");
        Map<String, Object> context = newHashMap();
        OpenApiModel openApiModel = openApiService.process(openAPI);
        context.put("openApi", openApiModel);
        String message = processTemplateIntoString(template, context);
        System.out.println();
        FileUtils.write(new File("/home/roman/adoc12/spec.adoc"), message, StandardCharsets.UTF_8);
    }

    private void login() {
        try {
            obtainOauth2Token();
        } catch (WebClientResponseException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            return;
        }
        fail("Expected 403 http error code while login via tfa");
    }

    private void confirmTfaCode(String code) {
        WebClient tokenClient = createWebClient(StringUtils.EMPTY);
        TokenResponse tokenResponse = tokenClient.post()
                .uri(uriBuilder -> uriBuilder.path(TOKEN_URL)
                        .queryParam(GRANT_TYPE_PARAM, TFA_CODE_GRANT_TYPE)
                        .queryParam(TFA_CODE_PARAM, code)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, getBasicAuthorizationHeader())
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();
        assertThat(tokenResponse).isNotNull();
        assertThat(tokenResponse.getAccessToken()).isNotNull();
    }
}
