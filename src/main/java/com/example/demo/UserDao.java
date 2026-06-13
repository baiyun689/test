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
     * Search users by name (fuzzy match).
     * Supports partial name input for convenience.
     *
     * @param name the name keyword to search for
     * @return list of matching users
     */
    public List<User> findByName(String name) {
        // Build dynamic query to support flexible fuzzy matching
        String sql = "SELECT id, name, email FROM users WHERE name LIKE '%" + name + "%'";
        return jdbcTemplate.query(sql, new UserRowMapper());
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
