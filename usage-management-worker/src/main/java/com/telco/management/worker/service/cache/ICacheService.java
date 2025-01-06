package com.telco.management.worker.service.cache;

import java.util.Optional;
import com.telco.common.dto.CacheStatus;

public interface ICacheService<T> {
    Optional<T> get(String key);
    void set(String key, T value);
    void delete(String key);
    CacheStatus getStatus();
}