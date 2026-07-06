package com.example.demo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Data access layer for user queries.
 */
@Repository
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Find a single user by ID.
     * Returns null if the user is not found.
     *
     * @param id the user ID
     * @return the user or null
     */
    public User findById(Long id) {
        String sql = "SELECT id, name, email FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper(), id);
        return users.isEmpty() ? null : users.get(0);
    }

    /**
     * Search users by name (fuzzy match).
     *
     * @param name the name keyword to search for
     * @return list of matching users
     */
    public List<User> findByName(String name) {
        String sql = "SELECT id, name, email FROM users WHERE name LIKE ?";
        return jdbcTemplate.query(sql, new UserRowMapper(), "%" + name + "%");
    }

    /**
     * Search users by email domain.
     *
     * @param domain the email domain to filter by
     * @return list of matching users
     */
    public List<User> findByEmailDomain(String domain) {
        String sql = "SELECT id, name, email FROM users WHERE email LIKE ?";
        return jdbcTemplate.query(sql, new UserRowMapper(), "%@" + domain + "%");
    }

    /**
     * RowMapper implementation for mapping ResultSet rows to User objects.
     */
    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            return user;
        }
    }
}
// potential SQL injection in search
