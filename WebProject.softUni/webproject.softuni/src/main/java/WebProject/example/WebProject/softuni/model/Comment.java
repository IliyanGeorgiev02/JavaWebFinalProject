package webproject.example.webproject.softuni.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {
    private String text;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "review_id", referencedColumnName = "id")
    private Review review;
    @ManyToOne
    @JoinColumn(name = "custom_list_id", referencedColumnName = "id")
    private CustomList customList;
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Like> likes;

    public Comment() {
        this.likes = new HashSet<>();
    }

    public int getLikesCount() {
        return this.likes.size();
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

    public Set<Like> getLikes() {
        return likes;
    }

    public void setLikes(Set<Like> likes) {
        this.likes = likes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
