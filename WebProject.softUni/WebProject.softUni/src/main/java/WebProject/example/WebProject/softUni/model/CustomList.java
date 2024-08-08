package WebProject.example.WebProject.softUni.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lists")
public class CustomList extends BaseEntity {
    private String title;
    private String description;
    private int likes;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(name = "lists_movies", joinColumns = @JoinColumn(name = "list_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id", referencedColumnName = "id"))
    private List<Movie> movies;
    @OneToMany(mappedBy = "customList", cascade = CascadeType.REMOVE, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<Comment> comments;

    public CustomList() {
        this.movies = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "CustomList{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", likes=" + likes +
                ", user=" + user +
                ", movies=" + movies +
                ", comments=" + comments +
                '}';
    }

}
