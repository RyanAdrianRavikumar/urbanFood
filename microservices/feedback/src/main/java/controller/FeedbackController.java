package controller;

import entity.Feedback;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.FeedbackService;

@RestController
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping(path = "/customer/feedback")
    public ResponseEntity<String> addFeedback(@Valid @RequestBody Feedback feedback) {
        return ResponseEntity.ok(feedbackService.addFeedback(feedback));
    }

    @GetMapping
    public ResponseEntity<String> getAllFeedback() {
        return ResponseEntity.ok(feedbackService.getAllFeedback());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<String> getByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(feedbackService.getFeedbackByCustomer(customerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateFeedback(
            @PathVariable String id,
            @RequestParam String newMessage) {
        return ResponseEntity.ok(feedbackService.updateFeedback(id, newMessage));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFeedback(@PathVariable String id) {
        return ResponseEntity.ok(feedbackService.deleteFeedback(id));
    }
   /* @GetMapping("/data")
    public List<Feedback> getAllFeedbackData() {
        return feedbackService.getAllFeedbackData();
    }
*/
}