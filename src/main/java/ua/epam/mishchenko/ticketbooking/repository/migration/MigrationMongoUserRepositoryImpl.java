package ua.epam.mishchenko.ticketbooking.repository.migration;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.stereotype.Repository;
import ua.epam.mishchenko.ticketbooking.model.User;
import ua.epam.mishchenko.ticketbooking.model.migration.UserMongo;

import java.util.List;

@Repository
public class MigrationMongoUserRepositoryImpl {

    private final MongoTemplate mongoTemplate;

    public MigrationMongoUserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<UserAggregation> getAggregatedData() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.project()
                    .andExpression("substr(email, indexOfBytes(email, '@') + 1, strLenBytes(email))").as("emailDomain"),
                Aggregation.group("emailDomain").count().as("userAmount")
        );
        AggregationResults<UserAggregation> userAggregations =
                mongoTemplate.aggregate(aggregation, UserMongo.class, UserAggregation.class);
        return userAggregations.getMappedResults();

    }

    public static class UserAggregation {
        @Field("_id")
        private String email;
        private int userAmount;

        public int getUserAmount() {
            return userAmount;
        }

        public void setUserAmount(int userAmount) {
            this.userAmount = userAmount;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

}
