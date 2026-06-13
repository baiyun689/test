package com.example.demo;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * Business service for user-related operations.
 */
@Service
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Search users by name keyword.
     * Returns an empty list if the keyword is blank.
     *
     * @param name the search keyword
     * @return list of matching users
     */
    public List<User> searchByName(String name) {
        if (!StringUtils.hasText(name)) {
            return Collections.emptyList();
        }
        return userDao.findByName(name);
    }
}
