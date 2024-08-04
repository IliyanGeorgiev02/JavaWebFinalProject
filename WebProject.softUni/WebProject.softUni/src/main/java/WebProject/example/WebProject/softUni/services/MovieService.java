package WebProject.example.WebProject.softUni.services;

import WebProject.example.WebProject.softUni.model.Movie;
import WebProject.example.WebProject.softUni.repositories.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public void saveMovie(Movie mappedMovie) {
        this.movieRepository.saveAndFlush(mappedMovie);
    }

    public Optional<Movie> findMovie(Movie mappedMovie) {
        return this.movieRepository.findByImdbID(mappedMovie.getImdbId());
    }
}
