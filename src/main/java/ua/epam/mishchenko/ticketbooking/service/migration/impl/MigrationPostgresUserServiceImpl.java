package ua.epam.mishchenko.ticketbooking.service.migration.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ua.epam.mishchenko.ticketbooking.service.migration.MigrationPostgresUserService;

import java.util.List;
import java.util.Map;

@Service
public class MigrationPostgresUserServiceImpl implements MigrationPostgresUserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> getAllUsers() {
        String sqlQuery = "SELECT id, email, name FROM users";
        return jdbcTemplate.queryForList(sqlQuery);
    }

}
