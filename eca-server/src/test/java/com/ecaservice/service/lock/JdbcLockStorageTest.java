package com.ecaservice.service.lock;

import com.ecaservice.service.AbstractJpaTest;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link JdbcLockStorage} functionality.
 *
 * @author Roman Batygin
 */
@Import(JdbcLockStorage.class)
class JdbcLockStorageTest extends AbstractJpaTest {

    private static final String LOCK_NAME = "lock";
    private static final String LOCK_KEY = "key";
    private static final long EXPIRATION = 0L;

    private static final String SELECT_COUNT_QUERY = "SELECT count(*) FROM lock";
    private static final String CLEAR_LOCK_TABLE_QUERY = "DELETE FROM lock";

    @Inject
    private JdbcLockStorage jdbcLockStorage;
    @Inject
    private JdbcTemplate jdbcTemplate;

    @Override
    public void deleteAll() {
        jdbcTemplate.update(CLEAR_LOCK_TABLE_QUERY);
    }

    @Test
    void testLock() {
        internalTestLock(EXPIRATION);
    }

    @Test
    void testExistingLock() {
        jdbcLockStorage.lock(LOCK_NAME, LOCK_KEY, EXPIRATION);
        boolean locked = jdbcLockStorage.lock(LOCK_NAME, LOCK_KEY, EXPIRATION);
        assertThat(locked).isFalse();
        Integer result = jdbcTemplate.queryForObject(SELECT_COUNT_QUERY, Integer.class);
        assertThat(result).isOne();
    }

    @Test
    void testUnlock() {
        internalTestLock(EXPIRATION);
        jdbcLockStorage.unlock(LOCK_NAME, LOCK_KEY);
        Integer result = jdbcTemplate.queryForObject(SELECT_COUNT_QUERY, Integer.class);
        assertThat(result).isZero();
    }

    @Test
    void testResetLockIfExpired() {
        internalTestLock(-1000L);
        jdbcLockStorage.resetLockIfExpired(LOCK_NAME, LOCK_KEY);
        Integer result = jdbcTemplate.queryForObject(SELECT_COUNT_QUERY, Integer.class);
        assertThat(result).isZero();
    }

    private void internalTestLock(long expiration) {
        boolean locked = jdbcLockStorage.lock(LOCK_NAME, LOCK_KEY, expiration);
        assertThat(locked).isTrue();
        Integer result = jdbcTemplate.queryForObject(SELECT_COUNT_QUERY, Integer.class);
        assertThat(result).isOne();
    }
}
