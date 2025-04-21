package service;

import entity.Review;
import org.springframework.stereotype.Service;
import repository.ReviewRepository;
import java.time.LocalDateTime;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public String addReview(Review review) {
        review.setReviewDate(LocalDateTime.now());
        reviewRepository.save(review);
        return generateInsertCommand(review);
    }

    public String getProductReviews(String productId) {
        reviewRepository.findByProductId(productId);
        return String.format("db.product_reviews.find({ productId: '%s' })", productId);
    }

    public String getTopRatedReviews(int minRating) {
        reviewRepository.findByRatingGreaterThanEqual(minRating);
        return String.format("db.product_reviews.find({ rating: { $gte: %d } })", minRating);
    }

    public String searchReviews(String keyword) {
        reviewRepository.searchReviews(keyword);
        return String.format("db.product_reviews.find({ reviewText: { $regex: '%s', $options: 'i' } })", keyword);
    }

    private String generateInsertCommand(Review review) {
        return String.format(
                "db.product_reviews.insertOne({ " +
                        "productId: '%s', " +
                        "userId: '%s', " +
                        "username: '%s', " +
                        "rating: %d, " +
                        "reviewText: '%s', " +
                        "reviewDate: new Date(), " +
                        "verifiedPurchase: %b })",
                review.getProductId(),
                review.getUserId(),
                review.getUsername(),
                review.getRating(),
                review.getReviewText(),
                review.getVerifiedPurchase()
        );
    }
    public List<Review> getAllReviewData() {
        return reviewRepository.findAll();
    }
}