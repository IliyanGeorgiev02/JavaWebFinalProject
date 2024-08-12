package WebProject.example.WebProject.softUni.dtos;

import jakarta.validation.constraints.*;

public class AddReviewDto {
    @NotBlank(message = "Your review must contain a title")
    private String reviewTitle;
    @NotNull(message = "Your review must contain a rating")
    @Min(1)
    @Max(100)
    private int reviewRating;
    @NotBlank(message = "Your review must contain text")
    private String reviewText;
    @NotBlank
    private String movieTitle;
    @NotBlank
    private String movieYear;

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieYear() {
        return movieYear;
    }

    public void setMovieYear(String movieYear) {
        this.movieYear = movieYear;
    }

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
