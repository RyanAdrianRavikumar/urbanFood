package repository;

import entity.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByProductId(String productId);
    List<Review> findByUserId(String userId);
    List<Review> findByRating(Integer rating);
    List<Review> findByVerifiedPurchase(boolean verified);
    List<Review> findByRatingGreaterThanEqual(int minRating);

    @Query("{ 'reviewText' : { $regex: ?0, $options: 'i' } }")
    List<Review> searchReviews(String keyword);
}