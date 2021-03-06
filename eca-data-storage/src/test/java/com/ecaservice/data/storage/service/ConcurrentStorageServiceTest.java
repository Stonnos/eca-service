package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.AbstractJpaTest;
import com.ecaservice.data.storage.config.StorageTestConfiguration;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.exception.TableExistsException;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.data.storage.service.impl.StorageServiceImpl;
import com.ecaservice.data.storage.service.impl.TableNameTestService;
import eca.data.db.SqlQueryHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import weka.core.Instances;

import javax.inject.Inject;
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
@Import({StorageServiceImpl.class, InstancesService.class, TransactionalService.class,
        SqlQueryHelper.class, StorageTestConfiguration.class, TableNameTestService.class})
class ConcurrentStorageServiceTest extends AbstractJpaTest {

    private static final int NUM_THREADS = 2;
    private static final String TEST_TABLE = "test_table";
    private static final String NEW_TABLE_NAME = "new_table_name";
    private static final String TEST_TABLE_2 = "test_table2";
    private static final String USER_NAME = "admin";

    @Inject
    private StorageServiceImpl storageService;

    @Inject
    private InstancesRepository instancesRepository;

    @MockBean
    private UserService userService;

    private ConcurrentStorageService concurrentStorageService;

    @Override
    public void init() {
        concurrentStorageService = new ConcurrentStorageService(storageService);
    }

    @Override
    public void deleteAll() {
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
                    concurrentStorageService.saveData(instances, TABLE_NAME);
                } catch (TableExistsException ex) {
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
        InstancesEntity first = createAndSaveInstancesEntity(TEST_TABLE);
        InstancesEntity second = createAndSaveInstancesEntity(TEST_TABLE_2);
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
                concurrentStorageService.renameData(instancesEntity.getId(), NEW_TABLE_NAME);
            } catch (TableExistsException ex) {
                tableExistsErrors.incrementAndGet();
            } finally {
                countDownLatch.countDown();
            }
        });
    }

    private InstancesEntity createAndSaveInstancesEntity(String tableName) {
        InstancesEntity instancesEntity = createInstancesEntity();
        instancesEntity.setTableName(tableName);
        return instancesRepository.save(instancesEntity);
    }
}
