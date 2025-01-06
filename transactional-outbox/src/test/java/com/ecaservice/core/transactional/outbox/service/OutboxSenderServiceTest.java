package com.ecaservice.core.transactional.outbox.service;

import com.ecaservice.core.transactional.outbox.AbstractJpaTest;
import com.ecaservice.core.transactional.outbox.config.TransactionalOutboxProperties;
import com.ecaservice.core.transactional.outbox.entity.OutboxMessageEntity;
import com.ecaservice.core.transactional.outbox.repository.OutboxMessageRepository;
import com.ecaservice.core.transactional.outbox.test.model.TestMessage;
import com.ecaservice.core.transactional.outbox.test.service.TestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static com.ecaservice.common.web.util.JsonUtils.toJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link OutboxSenderService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith({SpringExtension.class})
@Import({OutboxSenderService.class, OutboxSenderProvider.class,
        OutboxMessageService.class, TransactionalOutboxProperties.class, TestService.class})
class OutboxSenderServiceTest extends AbstractJpaTest {

    private static final String MESSAGE_CODE = "REQUEST_1";

    @Autowired
    private OutboxMessageRepository outboxMessageRepository;

    @SpyBean
    private TestService testService;

    @Autowired
    private OutboxSenderService outboxSenderService;

    @Override
    public void deleteAll() {
        outboxMessageRepository.deleteAll();
    }

    @Test
    void testSentMessage() {
        OutboxMessageEntity outboxMessageEntity = saveMessage();
        outboxSenderService.sentOutboxMessage(outboxMessageEntity);
        verify(testService, atLeastOnce()).method(any(TestMessage.class));
        assertThat(outboxMessageRepository.count()).isZero();
    }

    private OutboxMessageEntity saveMessage() {
        OutboxMessageEntity outboxMessageEntity = new OutboxMessageEntity();
        outboxMessageEntity.setMessageCode(MESSAGE_CODE);
        outboxMessageEntity.setMessageBody(toJson(new TestMessage()));
        outboxMessageEntity.setCreatedAt(LocalDateTime.now());
        return outboxMessageRepository.save(outboxMessageEntity);
    }
}
