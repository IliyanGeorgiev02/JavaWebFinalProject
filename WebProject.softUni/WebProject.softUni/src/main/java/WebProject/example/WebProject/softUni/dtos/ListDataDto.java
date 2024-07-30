package WebProject.example.WebProject.softUni.dtos;
import WebProject.example.WebProject.softUni.model.Movie;

import java.util.List;
public class ListDataDto {
    private String title;
    private String description;
    private List<Movie> movies;

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
}
