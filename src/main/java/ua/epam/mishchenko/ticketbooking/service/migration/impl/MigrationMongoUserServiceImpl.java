package ua.epam.mishchenko.ticketbooking.service.migration.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneModel;
import org.bson.Document;
import org.springframework.stereotype.Service;
import ua.epam.mishchenko.ticketbooking.service.migration.MigrationMongoUserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MigrationMongoUserServiceImpl implements MigrationMongoUserService {

    @Override
    public void migrateData(List<Map<String, Object>> users) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("ticket_booking");
        MongoCollection<Document> collection = database.getCollection("users");

        List<InsertOneModel<Document>> bulkOperations = new ArrayList<>();

        for(Map<String, Object> user : users) {
            Document userDocument = new Document()
                    .append("id", user.get("id"))
                    .append("name", user.get("name"))
                    .append("email", user.get("email"));
            bulkOperations.add(new InsertOneModel<>(userDocument));
        }

        collection.bulkWrite(bulkOperations);

        mongoClient.close();
    }

}
