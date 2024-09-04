package webproject.example.webproject.softuni.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "likes")
public class Like extends BaseEntity {

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User liked;
    @ManyToOne
    @JoinColumn(name = "review_id", referencedColumnName = "id")
    private Review review;
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

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
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
