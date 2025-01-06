package com.ecaservice.core.transactional.outbox.service;

import com.ecaservice.core.transactional.outbox.AbstractJpaTest;
import com.ecaservice.core.transactional.outbox.config.TransactionalOutboxProperties;
import com.ecaservice.core.transactional.outbox.entity.OutboxMessageEntity;
import com.ecaservice.core.transactional.outbox.repository.OutboxMessageRepository;
import com.ecaservice.core.transactional.outbox.test.model.TestMessage;
import com.ecaservice.core.transactional.outbox.test.service.TestService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static com.ecaservice.common.web.util.JsonUtils.toJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link OutboxMessageProcessor} class.
 *
 * @author Roman Batygin
 */
@Import({OutboxSenderService.class, OutboxSenderProvider.class,
        OutboxMessageProcessor.class, TestService.class,
        OutboxMessageService.class, TransactionalOutboxProperties.class, TestService.class})
class OutboxMessageProcessorTest extends AbstractJpaTest {

    private static final String MESSAGE_CODE = "REQUEST_1";

    private static final int MESSAGES = 50;
    private static final int NUM_THREADS = 3;

    @Autowired
    private OutboxMessageRepository outboxMessageRepository;

    @Autowired
    private OutboxMessageService outboxMessageService;

    @SpyBean
    private OutboxSenderService outboxSenderService;

    @Autowired
    private OutboxMessageProcessor outboxMessageProcessor;

    @Captor
    private ArgumentCaptor<OutboxMessageEntity> outboxMessageEntityArgumentCaptor;

    @Override
    public void deleteAll() {
        outboxMessageRepository.deleteAll();
    }

    @Test
    void testProcessNotSentMessages() {
        IntStream.range(0, MESSAGES).forEach(i -> saveMessage());
        outboxMessageProcessor.processNotSentMessages();
        verify(outboxSenderService, times(MESSAGES)).sentOutboxMessage(outboxMessageEntityArgumentCaptor.capture());
        var requests = outboxMessageEntityArgumentCaptor.getAllValues();
        checkUniqueIds(requests);
    }

    @Test
    void testProcessNotSentMessagesInMultiThreadMode() {
        IntStream.range(0, MESSAGES).forEach(i -> saveMessage());
        List<CompletableFuture<Void>> futures = IntStream.range(0, NUM_THREADS)
                .mapToObj(i -> CompletableFuture.runAsync(() -> outboxMessageProcessor.processNotSentMessages()))
                .toList();
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.join();
        verify(outboxSenderService, times(MESSAGES)).sentOutboxMessage(outboxMessageEntityArgumentCaptor.capture());
        var messages = outboxMessageEntityArgumentCaptor.getAllValues();
        assertThat(messages).hasSize(MESSAGES);
        checkUniqueIds(messages);
    }

    private void checkUniqueIds(List<OutboxMessageEntity> outboxMessageEntities) {
        Set<Long> uniqueIds = new HashSet<>();
        for (OutboxMessageEntity outboxMessageEntity : outboxMessageEntities) {
            assertThat(uniqueIds.add(outboxMessageEntity.getId())).isTrue();
        }
    }

    private void saveMessage() {
        OutboxMessageEntity outboxMessageEntity = new OutboxMessageEntity();
        outboxMessageEntity.setMessageCode(MESSAGE_CODE);
        outboxMessageEntity.setMessageBody(toJson(new TestMessage()));
        outboxMessageEntity.setCreatedAt(LocalDateTime.now());
        outboxMessageRepository.save(outboxMessageEntity);
    }
}
