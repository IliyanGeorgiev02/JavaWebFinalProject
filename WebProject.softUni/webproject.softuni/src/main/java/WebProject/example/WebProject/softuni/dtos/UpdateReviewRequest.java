package webproject.example.webproject.softuni.dtos;

public class UpdateReviewRequest {

    private EditReviewDto newReviewData;
    private ReviewFullInfoDto reviewFullInfoDto;

    // Constructors, Getters, and Setters
    public UpdateReviewRequest(EditReviewDto newReviewData, ReviewFullInfoDto reviewFullInfoDto) {
        this.newReviewData = newReviewData;
        this.reviewFullInfoDto = reviewFullInfoDto;
    }

    public EditReviewDto getNewReviewData() {
        return newReviewData;
    }

    public void setNewReviewData(EditReviewDto newReviewData) {
        this.newReviewData = newReviewData;
    }

    public ReviewFullInfoDto getReviewFullInfoDto() {
        return reviewFullInfoDto;
    }

    public void setReviewFullInfoDto(ReviewFullInfoDto reviewFullInfoDto) {
        this.reviewFullInfoDto = reviewFullInfoDto;
    }
}
