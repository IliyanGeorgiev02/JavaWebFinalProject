package webproject.example.webproject.softuni.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class EditReviewDto {
    @NotEmpty(message = "Your review must contain a title")
    private String reviewTitle;
    @NotNull(message = "Your review must contain a rating")
    @Min(1)
    @Max(100)
    private int reviewRating;
    @NotEmpty(message = "Your review must contain text")
    private String reviewText;

    public String getReviewTitle() {
        return reviewTitle;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public int getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(int reviewRating) {
        this.reviewRating = reviewRating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
}
