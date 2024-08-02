package WebProject.example.WebProject.softUni.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {
    private String text;
    private int likes;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "review_id", referencedColumnName = "id")
    private Review review;
    @ManyToOne
    @JoinColumn(name = "custom_list_id", referencedColumnName = "id")
    private CustomList customList;

    public Comment() {
    }

    public CustomList getCustomList() {
        return customList;
    }

    public void setCustomList(CustomList customList) {
        this.customList = customList;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
