package entity;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "product_reviews")
@Data
public class Review {
    @Id
    private String id;

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Username is required")
    private String username;

    @NotNull(message = "Rating is required")
    @Min(1) @Max(5)
    private Integer rating;

    @NotBlank(message = "Review text is required")
    @Size(min = 20, max = 1000, message = "Review must be 20-1000 characters")
    private String reviewText;

    private LocalDateTime reviewDate;
    private Boolean verifiedPurchase = false;
    private List<String> upvotedBy = new ArrayList<>();
}