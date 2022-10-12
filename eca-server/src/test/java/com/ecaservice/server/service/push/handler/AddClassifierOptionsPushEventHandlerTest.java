package com.ecaservice.server.service.push.handler;

import com.ecaservice.server.event.model.push.AddClassifierOptionsPushEvent;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.model.entity.ClassifiersConfigurationActionType;
import com.ecaservice.server.repository.ClassifiersConfigurationHistoryRepository;
import com.ecaservice.server.repository.ClassifiersConfigurationRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.classifiers.ClassifiersTemplateProvider;
import com.ecaservice.server.service.message.template.MessageTemplateProcessor;
import com.ecaservice.web.dto.model.FormTemplateDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.List;

import static com.ecaservice.server.TestHelperUtils.createClassifiersConfiguration;
import static com.ecaservice.server.TestHelperUtils.createClassifiersConfigurationHistory;
import static com.ecaservice.server.service.push.dictionary.PushProperties.CLASSIFIERS_CONFIGURATION_ID_PROPERTY;
import static com.ecaservice.server.service.push.dictionary.PushProperties.CLASSIFIER_CONFIGURATION_CHANGE_MESSAGE_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AddClassifierOptionsPushEventHandler} class.
 *
 * @author Roman Batygin
 */
@Import(AddClassifierOptionsPushEventHandler.class)
class AddClassifierOptionsPushEventHandlerTest extends AbstractJpaTest {

    private static final String CURRENT_USER = "currentUser";
    private static final String USER_1 = "user1";
    private static final String USER_2 = "user2";
    private static final long CLASSIFIER_OPTIONS_ID = 1L;
    private static final String OPTIONS_NAME = "optionsName";
    private static final String TEMPLATE_TITLE = "title";

    @MockBean
    private MessageTemplateProcessor messageTemplateProcessor;
    @MockBean
    private ClassifiersTemplateProvider classifiersTemplateProvider;

    @Inject
    private ClassifiersConfigurationRepository classifiersConfigurationRepository;
    @Inject
    private ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository;

    @Inject
    private AddClassifierOptionsPushEventHandler addClassifierOptionsPushEventHandler;

    private ClassifiersConfiguration classifiersConfiguration;

    @Override
    public void deleteAll() {
        classifiersConfigurationHistoryRepository.deleteAll();
        classifiersConfigurationRepository.deleteAll();
    }

    @Override
    public void init() {
        saveData();
        var formTemplateDto = new FormTemplateDto();
        formTemplateDto.setTemplateTitle(TEMPLATE_TITLE);
        when(classifiersTemplateProvider.getClassifierTemplateByClass(anyString())).thenReturn(formTemplateDto);
    }

    @Test
    void testHandleAddOptionsEvent() {
        var event =
                new AddClassifierOptionsPushEvent(this, CURRENT_USER, classifiersConfiguration, CLASSIFIER_OPTIONS_ID,
                        OPTIONS_NAME);
        var pushRequest = addClassifierOptionsPushEventHandler.handleEvent(event);
        assertThat(pushRequest).isNotNull();
        assertThat(pushRequest.getInitiator()).isEqualTo(event.getInitiator());
        assertThat(pushRequest.getCreated()).isNotNull();
        assertThat(pushRequest.getRequestId()).isNotNull();
        assertThat(pushRequest.getMessageType()).isEqualTo(CLASSIFIER_CONFIGURATION_CHANGE_MESSAGE_TYPE);
        assertThat(pushRequest.getReceivers()).hasSize(2);
        assertThat(pushRequest.getReceivers()).containsAll(List.of(USER_1, USER_2));
        assertThat(pushRequest.getAdditionalProperties())
                .containsEntry(CLASSIFIERS_CONFIGURATION_ID_PROPERTY, String.valueOf(classifiersConfiguration.getId()));
    }

    private void saveData() {
        ClassifiersConfiguration configuration = createClassifiersConfiguration();
        classifiersConfiguration = classifiersConfigurationRepository.save(configuration);
        var history = List.of(
                createClassifiersConfigurationHistory(configuration,
                        ClassifiersConfigurationActionType.ADD_CLASSIFIER_OPTIONS, USER_1),
                createClassifiersConfigurationHistory(configuration,
                        ClassifiersConfigurationActionType.ADD_CLASSIFIER_OPTIONS, USER_1),
                createClassifiersConfigurationHistory(configuration,
                        ClassifiersConfigurationActionType.ADD_CLASSIFIER_OPTIONS, USER_2),
                createClassifiersConfigurationHistory(configuration,
                        ClassifiersConfigurationActionType.ADD_CLASSIFIER_OPTIONS, CURRENT_USER)
        );
        classifiersConfigurationHistoryRepository.saveAll(history);
    }
}
