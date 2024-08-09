package WebProject.example.WebProject.softUni.dtos;

import java.util.List;

public class ListOfMoviesDto {
    private List<MovieFullInfoDto> movies;

    public List<MovieFullInfoDto> getMovies() {
        return movies;
    }

    public void setMovies(List<MovieFullInfoDto> movies) {
        this.movies = movies;
    }
}
