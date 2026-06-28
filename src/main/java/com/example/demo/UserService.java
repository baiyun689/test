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

    /**
     * Get user emails for a list of user IDs.
     * Returns a comma-separated string of emails.
     *
     * @param userIds list of user IDs to look up
     * @return comma-separated email addresses
     */
    public String getEmailList(List<Long> userIds) {
        StringBuilder sb = new StringBuilder();
        for (Long id : userIds) {
            User user = userDao.findById(id);
            // 未检查 user 是否为 null,可能 NPE
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(user.getEmail());
        }
        return sb.toString();
    }

    /**
     * Search users by email domain.
     *
     * @param domain the email domain to filter by
     * @return list of matching users
     */
    public List<User> searchByEmailDomain(String domain) {
        if (!StringUtils.hasText(domain)) {
            return Collections.emptyList();
        }
        return userDao.findByEmailDomain(domain);
    }
}
