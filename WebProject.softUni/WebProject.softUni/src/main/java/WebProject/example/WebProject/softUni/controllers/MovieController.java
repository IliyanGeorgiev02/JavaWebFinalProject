package WebProject.example.WebProject.softUni.controllers;

import WebProject.example.WebProject.softUni.model.Movie;
import WebProject.example.WebProject.softUni.services.MovieService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/Movie/{id}")
    public String getMovie(@PathVariable("id") long id, Model model) {
        Optional<Movie> movieById = this.movieService.findMovieById(id);
        if (movieById.isPresent()) {
            model.addAttribute("movieData", movieById.get());
            return "Movie";
        }
        return "Home";
    }

    @GetMapping("/Movies")
    public String getMoviesPage(Model model) {
        List<Movie> allMovies = this.movieService.findAllMovies();
        model.addAttribute("moviesData", allMovies);
        return "Movies";
    }
}
