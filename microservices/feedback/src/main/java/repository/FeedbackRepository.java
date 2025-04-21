package repository;

import entity.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FeedbackRepository extends MongoRepository<Feedback, String> {

    // Custom query methods
    List<Feedback> findByCustomerId(String customerId);
    List<Feedback> findByRatingGreaterThanEqual(int minRating);
    List<Feedback> findByCategory(String category);

    // This will show the MongoDB query in action
    @Query(value = "{ 'message' : { $regex: ?0, $options: 'i' } }")
    List<Feedback> findFeedbackContainingText(String text);
}