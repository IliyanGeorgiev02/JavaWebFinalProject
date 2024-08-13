package webproject.example.webproject.softuni.dtos;

import java.util.List;

public class DisplayListDto {
    private long id;
    private String title;
    private String description;
    private int likes;
    private String username;
    private long userId;
    private List<DisplayMovieInListDto> movies;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<DisplayMovieInListDto> getMovies() {
        return movies;
    }

    public void setMovies(List<DisplayMovieInListDto> movies) {
        this.movies = movies;
    }
}
