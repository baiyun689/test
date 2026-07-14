package com.example.demo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserCacheService {

    private final Map<String, User> cache = new ConcurrentHashMap<>();
    private final UserDao userDao;

    public UserCacheService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User get(String userId) {
        User cached = cache.get(userId);
        if (cached != null) {
            return cached;
        }
        User dbUser = userDao.findById(Long.parseLong(userId));
        cache.put(userId, dbUser);
        return dbUser;
    }

    public void evict(String userId) {
        cache.remove(userId);
    }

    public int size() {
        return cache.size();
    }
}
