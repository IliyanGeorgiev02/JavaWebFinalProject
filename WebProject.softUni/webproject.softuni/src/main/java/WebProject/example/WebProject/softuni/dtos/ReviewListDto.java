package webproject.example.webproject.softuni.dtos;

import java.util.List;

public class ReviewListDto {
    private List<ReviewFullInfoDto> reviews;

    public List<ReviewFullInfoDto> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewFullInfoDto> reviews) {
        this.reviews = reviews;
    }
}
