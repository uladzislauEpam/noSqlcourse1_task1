package ua.epam.mishchenko.ticketbooking.web.controller.migration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.epam.mishchenko.ticketbooking.repository.migration.MigrationMongoUserRepositoryImpl;
import ua.epam.mishchenko.ticketbooking.service.migration.MigrationMongoUserService;
import ua.epam.mishchenko.ticketbooking.service.migration.MigrationPostgresUserService;

import java.util.List;

@RestController
@RequestMapping("api/migration")
public class MigrationController {

    @Autowired
    MigrationMongoUserService migrationMongoUserService;

    @Autowired
    MigrationPostgresUserService migrationPostgresUserService;

    @Autowired
    MigrationMongoUserRepositoryImpl migrationMongoUserRepository;

    @GetMapping
    public String migrateData() {
        migrationMongoUserService.migrateData(migrationPostgresUserService.getAllUsers());
        return "Data migration page trigger";
    }

    @GetMapping("/agg")
    public List<MigrationMongoUserRepositoryImpl.UserAggregation> getAggregatedData() {
        return migrationMongoUserRepository.getAggregatedData();
    }

}
