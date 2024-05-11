package com.ecaservice.data.storage.service;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.lock.config.CoreLockAutoConfiguration;
import com.ecaservice.core.lock.metrics.LockMeterService;
import com.ecaservice.data.storage.AbstractJpaTest;
import com.ecaservice.data.storage.config.StorageTestConfiguration;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.exception.InstancesExistsException;
import com.ecaservice.data.storage.mapping.AttributeMapperImpl;
import com.ecaservice.data.storage.repository.AttributeRepository;
import com.ecaservice.data.storage.repository.AttributeValueRepository;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.data.storage.service.impl.StorageServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import weka.core.Instances;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ecaservice.data.storage.TestHelperUtils.createInstancesEntity;
import static com.ecaservice.data.storage.TestHelperUtils.loadInstances;
import static com.ecaservice.data.storage.entity.InstancesEntity_.TABLE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ConcurrentStorageService} functionality.
 *
 * @author Roman Batygin
 */
@EnableAspectJAutoProxy
@Import({StorageServiceImpl.class, InstancesService.class, InstancesBatchService.class,
        StorageTestConfiguration.class, ConcurrentStorageService.class,
        AttributeService.class, AttributeMapperImpl.class, InstancesTransformer.class,
        CoreLockAutoConfiguration.class})
class ConcurrentStorageServiceTest extends AbstractJpaTest {

    private static final int NUM_THREADS = 2;
    private static final String TEST_RELATION_NAME = "test_relation_name";
    private static final String NEW_RELATION_NAME = "new_relation_name";
    private static final String TEST_RELATION_NAME_2 = "test_relation_name_2";
    private static final String USER_NAME = "admin";

    @Autowired
    private InstancesRepository instancesRepository;
    @Autowired
    private AttributeRepository attributeRepository;
    @Autowired
    private AttributeValueRepository attributeValueRepository;

    @MockBean
    private UserService userService;
    @MockBean
    private FilterTemplateService filterTemplateService;
    @MockBean
    private LockMeterService lockMeterService;
    @MockBean
    private SearchQueryCreator searchQueryCreator;

    @Autowired
    private StorageService storageService;

    @Override
    public void deleteAll() {
        unsetInstancesClass();
        attributeValueRepository.deleteAll();
        attributeRepository.deleteAll();
        instancesRepository.deleteAll();
    }

    @Test
    void testConcurrentSaveData() throws Exception {
        Instances instances = loadInstances();
        when(userService.getCurrentUser()).thenReturn(USER_NAME);
        final AtomicInteger tableExistsErrors = new AtomicInteger();
        final CountDownLatch countDownLatch = new CountDownLatch(NUM_THREADS);
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                try {
                    storageService.saveData(instances, TABLE_NAME);
                } catch (InstancesExistsException ex) {
                    tableExistsErrors.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        executorService.shutdownNow();

        assertThat(tableExistsErrors.get()).isOne();
        assertThat(instancesRepository.count()).isOne();
    }

    @Test
    void testConcurrentRenameData() throws Exception {
        InstancesEntity first = createAndSaveInstancesEntity(TEST_RELATION_NAME);
        InstancesEntity second = createAndSaveInstancesEntity(TEST_RELATION_NAME_2);
        final AtomicInteger tableExistsErrors = new AtomicInteger();
        final CountDownLatch countDownLatch = new CountDownLatch(NUM_THREADS);
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        renameData(executorService, first, tableExistsErrors, countDownLatch);
        renameData(executorService, second, tableExistsErrors, countDownLatch);
        countDownLatch.await();
        executorService.shutdownNow();

        assertThat(tableExistsErrors.get()).isOne();
    }

    private void renameData(ExecutorService executorService, InstancesEntity instancesEntity,
                            AtomicInteger tableExistsErrors, CountDownLatch countDownLatch) {
        executorService.submit(() -> {
            try {
                storageService.renameData(instancesEntity.getId(), NEW_RELATION_NAME);
            } catch (InstancesExistsException ex) {
                tableExistsErrors.incrementAndGet();
            } finally {
                countDownLatch.countDown();
            }
        });
    }

    private InstancesEntity createAndSaveInstancesEntity(String relationName) {
        InstancesEntity instancesEntity = createInstancesEntity();
        instancesEntity.setTableName(relationName);
        instancesEntity.setRelationName(relationName);
        instancesEntity.setUuid(UUID.randomUUID().toString());
        instancesEntity.setIdColumnName(UUID.randomUUID().toString());
        return instancesRepository.save(instancesEntity);
    }

    private void unsetInstancesClass() {
        var instancesList = instancesRepository.findAll();
        instancesList.forEach(instancesEntity -> instancesEntity.setClassAttribute(null));
        instancesRepository.saveAll(instancesList);
    }
}
