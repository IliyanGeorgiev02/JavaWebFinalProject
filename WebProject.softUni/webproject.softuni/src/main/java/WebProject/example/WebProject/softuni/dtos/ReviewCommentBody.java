package webproject.example.webproject.softuni.dtos;

public class ReviewCommentBody {
    private Long reviewId;
    private Long commentId;

    public ReviewCommentBody(Long reviewId, Long commentId) {
        this.reviewId = reviewId;
        this.commentId = commentId;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }
}
