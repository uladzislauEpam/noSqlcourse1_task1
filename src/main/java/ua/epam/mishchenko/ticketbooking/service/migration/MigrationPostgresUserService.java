package ua.epam.mishchenko.ticketbooking.service.migration;

import java.util.List;
import java.util.Map;

public interface MigrationPostgresUserService {

    public List<Map<String, Object>> getAllUsers();

}
