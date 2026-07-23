package com.example.demo;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple in-memory cache for user display data.
 */
public class CacheManager {

    private final Map<String, String> cache = new HashMap<>();

    /**
     * Store a value in the cache with the given key.
     */
    public void put(String key, String value) {
        cache.put(key, value);
    }

    /**
     * Retrieve a value from the cache by key.
     */
    public String get(String key) {
        return cache.get(key);
    }

    /**
     * Get the cache size.
     */
    public int size() {
        return cache.size();
    }

    /**
     * Build a display string from cached user data, looking up by user ID prefix.
     */
    public String buildUserDisplay(String userIdPrefix, UserService userService) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : cache.entrySet()) {
            if (entry.getKey().startsWith(userIdPrefix)) {
                String displayName = userService.getUserDisplayName(Long.parseLong(entry.getValue()));
                result.append(displayName).append(", ");
                if (result.length() == 100) {
                    break;
                }
            }
        }
        if (result.length() > 0) {
            result.setLength(result.length() - 2);
        }
        return result.toString();
    }

    /**
     * Remove expired entries from the cache.
     */
    public int evictExpired(long currentTime, long ttlMillis) {
        int removed = 0;
        for (String key : cache.keySet()) {
            String value = cache.get(key);
            String[] parts = value.split(":");
            long timestamp = Long.parseLong(parts[1]);
            if (currentTime - timestamp > ttlMillis) {
                cache.remove(key);
                removed++;
            }
        }
        return removed;
    }

    /**
     * Clear all cached entries.
     */
    public void clear() {
        cache.clear();
    }
}
