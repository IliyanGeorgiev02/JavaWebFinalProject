package webproject.example.webproject.softuni.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "lists")
public class CustomList extends BaseEntity {
    private String title;
    private String description;
    private int likes;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @Column(name = "created_date")
    private LocalDate created;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "lists_movies", joinColumns = @JoinColumn(name = "list_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id", referencedColumnName = "id"))
    private Set<Movie> movies;
    @OneToMany(mappedBy = "customList", cascade = CascadeType.REMOVE, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<Comment> comments;

    public CustomList() {
        this.movies = new HashSet<>();
        this.comments = new ArrayList<>();
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
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

    public Set<Movie> getMovies() {
        return movies;
    }

    public void setMovies(Set<Movie> movies) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CustomList that = (CustomList) o;
        return likes == that.likes && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(user, that.user) && Objects.equals(movies, that.movies) && Objects.equals(comments, that.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, description, likes, user, movies, comments);
    }
}
