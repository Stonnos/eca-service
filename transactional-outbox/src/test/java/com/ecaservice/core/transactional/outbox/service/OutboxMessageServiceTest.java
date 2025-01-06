package com.ecaservice.core.transactional.outbox.service;

import com.ecaservice.core.transactional.outbox.AbstractJpaTest;
import com.ecaservice.core.transactional.outbox.config.TransactionalOutboxProperties;
import com.ecaservice.core.transactional.outbox.entity.OutboxMessageEntity;
import com.ecaservice.core.transactional.outbox.model.OutboxMessage;
import com.ecaservice.core.transactional.outbox.repository.OutboxMessageRepository;
import com.ecaservice.core.transactional.outbox.test.model.TestMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;

import static com.ecaservice.common.web.util.JsonUtils.toJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link OutboxMessageService} class.
 *
 * @author Roman Batygin
 */
@Import({OutboxMessageService.class, TransactionalOutboxProperties.class})
class OutboxMessageServiceTest extends AbstractJpaTest {

    private static final String MESSAGE_CODE = "REQUEST_1";

    @Autowired
    private OutboxMessageRepository outboxMessageRepository;

    @Autowired
    private OutboxMessageService outboxMessageService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public void deleteAll() {
        outboxMessageRepository.deleteAll();
    }

    @Test
    void testSaveMessages() {
        saveMessage();
        var messages = outboxMessageRepository.findAll();
        assertThat(messages).hasSize(1);
        OutboxMessageEntity outboxMessageEntity = messages.getFirst();
        assertThat(outboxMessageEntity.getMessageCode()).isEqualTo(MESSAGE_CODE);
        assertThat(outboxMessageEntity.getMessageBody()).isEqualTo(toJson(new TestMessage()));
        assertThat(outboxMessageEntity.getCreatedAt()).isNotNull();
    }

    @Test
    void testSaveMessagesWithoutTransaction() {
        assertThrows(IllegalTransactionStateException.class, () -> outboxMessageService.saveOutboxMessage(
                Collections.singletonList(new OutboxMessage(MESSAGE_CODE, new TestMessage()))));
    }

    @Test
    void testDeleteMessage() {
        saveMessage();
        var messages = outboxMessageRepository.findAll();
        assertThat(messages).hasSize(1);
        outboxMessageService.delete(messages.getFirst());
        assertThat(outboxMessageRepository.count()).isZero();
    }

    private void saveMessage() {
        transactionTemplate.execute(status -> {
            outboxMessageService.saveOutboxMessage(
                    Collections.singletonList(new OutboxMessage(MESSAGE_CODE, new TestMessage())));
            return null;
        });
    }
}
