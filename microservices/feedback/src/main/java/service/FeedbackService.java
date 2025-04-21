package service;

import entity.Feedback;
import org.springframework.stereotype.Service;
import repository.FeedbackRepository;

import java.util.List;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    // CREATE
    public String addFeedback(Feedback feedback) {
        feedbackRepository.save(feedback);
        return "db.customer_feedback.insertOne({ " +
                "customerId: '" + feedback.getCustomerId() + "', " +
                "message: '" + feedback.getMessage() + "', " +
                "rating: " + feedback.getRating() + ", " +
                "timestamp: new Date() })";
    }

    // READ
    public String getAllFeedback() {
        feedbackRepository.findAll();
        return "db.customer_feedback.find()";
    }

    // READ by customer
    public String getFeedbackByCustomer(String customerId) {
        feedbackRepository.findByCustomerId(customerId);
        return "db.customer_feedback.find({ customerId: '" + customerId + "' })";
    }

    // UPDATE
    public String updateFeedback(String id, String newMessage) {
        feedbackRepository.findById(id).ifPresent(f -> {
            f.setMessage(newMessage);
            feedbackRepository.save(f);
        });
        return "db.customer_feedback.updateOne({ _id: ObjectId('" + id + "') }, " +
                "{ $set: { message: '" + newMessage + "' } })";
    }

    // DELETE
    public String deleteFeedback(String id) {
        feedbackRepository.deleteById(id);
        return "db.customer_feedback.deleteOne({ _id: ObjectId('" + id + "') })";
    }
    public List<Feedback> getAllFeedbackData() {
        return feedbackRepository.findAll();
    }
}