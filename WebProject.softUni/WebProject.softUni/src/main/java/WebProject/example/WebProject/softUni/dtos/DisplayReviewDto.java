package WebProject.example.WebProject.softUni.dtos;

import java.util.List;

public class DisplayReviewDto {
    private List<MovieReviewInfoDto> reviews;

    public List<MovieReviewInfoDto> getReviews() {
        return reviews;
    }

    public void setReviews(List<MovieReviewInfoDto> reviews) {
        this.reviews = reviews;
    }
}
