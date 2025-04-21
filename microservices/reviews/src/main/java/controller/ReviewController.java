package controller;

import entity.Review;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.ReviewService;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<String> addReview(@Valid @RequestBody Review review) {
        return ResponseEntity.ok(reviewService.addReview(review));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<String> getProductReviews(@PathVariable String productId) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }

    @GetMapping("/top-rated")
    public ResponseEntity<String> getTopRatedReviews(
            @RequestParam(defaultValue = "4") int minRating) {
        return ResponseEntity.ok(reviewService.getTopRatedReviews(minRating));
    }

    @GetMapping("/search")
    public ResponseEntity<String> searchReviews(@RequestParam String keyword) {
        return ResponseEntity.ok(reviewService.searchReviews(keyword));
    }
    @GetMapping("/data")
    public List<Review> getAllReviewData() {
        return reviewService.getAllReviewData();
    }
    
}