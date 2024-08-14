package webproject.example.webproject.softuni.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import webproject.example.webproject.softuni.model.Movie;
import webproject.example.webproject.softuni.repositories.MovieRepository;
import webproject.example.webproject.softuni.services.MovieService;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomMovieScheduler {

    private final MovieRepository movieRepository;
    private final MovieService movieService;

    public RandomMovieScheduler(MovieRepository movieRepository, MovieService movieService) {
        this.movieRepository = movieRepository;
        this.movieService = movieService;
    }

    @Scheduled(cron = "@midnight")
    public void selectAndSaveRandomMovie() {
        List<Movie> movies = movieRepository.findAll();
        if (!movies.isEmpty()) {
            Movie randomMovie = movies.get(ThreadLocalRandom.current().nextInt(movies.size()-1));
            this.movieService.cacheSelectedMovie(randomMovie);
        }
    }


}
