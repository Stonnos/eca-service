package com.ecaservice.service.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

/**
 * Implements jdbc lock storage.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JdbcLockStorage implements LockStorage {

    private static final String LOCK_TABLE = "lock";

    private static final String INSERT_LOCK_QUERY = "INSERT INTO %s(lock_name, lock_key, expire_at) VALUES(?, ?, ?)";
    private static final String DELETE_LOCK_QUERY = "DELETE FROM %s WHERE lock_name = ? AND lock_key = ?";
    private static final String EXPIRE_LOCK_QUERY =
            "DELETE FROM %s WHERE lock_name = ? AND lock_key = ? AND expire_at < ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean lock(String name, String key, long expiration) {
        try {
            LocalDateTime expireAt = LocalDateTime.now().plus(expiration, ChronoField.MILLI_OF_DAY.getBaseUnit());;
            String sql = String.format(INSERT_LOCK_QUERY, LOCK_TABLE);
            jdbcTemplate.update(sql, name, key, expireAt);
            return true;
        } catch (DuplicateKeyException ex) {
            return false;
        }
    }

    @Override
    public void unlock(String name, String key) {
        String sql = String.format(DELETE_LOCK_QUERY, LOCK_TABLE);
        jdbcTemplate.update(sql, name, key);
    }

    @Override
    public void resetLockIfExpired(String name, String key) {
        String sql = String.format(EXPIRE_LOCK_QUERY, LOCK_TABLE);
        jdbcTemplate.update(sql, name, key, LocalDateTime.now());
    }
}
