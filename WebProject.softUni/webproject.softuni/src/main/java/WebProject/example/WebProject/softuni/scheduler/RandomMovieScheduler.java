package webproject.example.webproject.softuni.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(RandomMovieScheduler.class);

    public RandomMovieScheduler(MovieRepository movieRepository, MovieService movieService) {
        this.movieRepository = movieRepository;
        this.movieService = movieService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void selectAndSaveRandomMovie() {
        List<Movie> movies = movieRepository.findAll();
        if (!movies.isEmpty()) {
            Movie randomMovie = movies.get(ThreadLocalRandom.current().nextInt(movies.size()));
            this.movieService.cacheSelectedMovie(randomMovie);
            logger.info(randomMovie.getTitle());
        }
    }


}
