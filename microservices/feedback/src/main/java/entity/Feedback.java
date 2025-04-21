package entity;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "customer_feedback")
@Data
public class Feedback {
    @Id
    private String id;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "Message cannot be empty")
    @Size(min = 10, max = 500, message = "Message must be 10-500 characters")
    private String message;

    private LocalDateTime timestamp;

    @Min(1) @Max(5)
    private Integer rating;

    private String category;
}