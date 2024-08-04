package WebProject.example.WebProject.softUni.dtos;

public class AddReviewDto {
    private String title;
    private String rating;
    private String reviewText;
    private MovieFullInfoDto movie;

    public MovieFullInfoDto getMovie() {
        return movie;
    }

    public void setMovie(MovieFullInfoDto movie) {
        this.movie = movie;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
}
