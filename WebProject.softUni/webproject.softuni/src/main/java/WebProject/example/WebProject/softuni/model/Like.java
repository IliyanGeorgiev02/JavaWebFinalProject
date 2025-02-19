package webproject.example.webproject.softuni.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "likes")
public class Like extends BaseEntity {

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User liked;
    @Column(name = "review_id")
    private Long reviewId;
    @ManyToOne
    @JoinColumn(name = "customList_id", referencedColumnName = "id")
    private CustomList customList;
    @ManyToOne
    @JoinColumn(name = "comment_id", referencedColumnName = "id")
    private Comment comment;

    public User getLiked() {
        return liked;
    }

    public void setLiked(User liked) {
        this.liked = liked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Like like = (Like) o;
        return Objects.equals(liked, like.liked);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), liked);
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public CustomList getCustomList() {
        return customList;
    }

    public void setCustomList(CustomList customList) {
        this.customList = customList;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
