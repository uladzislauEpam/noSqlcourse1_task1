package ua.epam.mishchenko.ticketbooking.service.migration;

import java.util.List;
import java.util.Map;

public interface MigrationMongoUserService {

    public void migrateData(List<Map<String, Object>> users);

}
