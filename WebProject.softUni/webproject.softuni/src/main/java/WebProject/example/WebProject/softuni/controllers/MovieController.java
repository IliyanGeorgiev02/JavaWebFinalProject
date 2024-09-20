package webproject.example.webproject.softuni.controllers;

import webproject.example.webproject.softuni.clients.ReviewClient;
import webproject.example.webproject.softuni.dtos.ListOfMoviesDto;
import webproject.example.webproject.softuni.dtos.MovieFullInfoDto;
import webproject.example.webproject.softuni.dtos.ReviewFullInfoDto;
import webproject.example.webproject.softuni.dtos.ReviewListDto;
import webproject.example.webproject.softuni.model.Movie;
import webproject.example.webproject.softuni.services.MovieService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import webproject.example.webproject.softuni.services.UserHelperService;

import java.util.List;
import java.util.Optional;

@Controller
public class MovieController {
    private final MovieService movieService;
    private final UserHelperService userHelperService;
    private final ReviewClient reviewClient;

    public MovieController(MovieService movieService, UserHelperService userHelperService, ReviewClient reviewClient) {
        this.movieService = movieService;
        this.userHelperService = userHelperService;
        this.reviewClient = reviewClient;
    }

    @GetMapping("/Movie/{id}")
    public String getMovie(@PathVariable("id") long id, Model model) {
        Optional<Movie> movieById = this.movieService.findMovieById(id);
        if (movieById.isPresent()) {
            Movie movie = movieById.get();
            MovieFullInfoDto mappedMovie = this.movieService.mapMovieShortInfo(movie);
            List<ReviewFullInfoDto> allReviewsByMovieId = this.reviewClient.findALLReviewsByMovieId(id);
            ReviewListDto reviewsData = this.reviewClient.mapReviewsToDto(allReviewsByMovieId);
            model.addAttribute("movieData", mappedMovie);
            model.addAttribute("reviewsData", reviewsData);
            return "Movie";
        }
        model.addAttribute("movieData", null);
        return "Movie";
    }

    @GetMapping("/Movies")
    public String getMoviesPage(Model model) {
        List<Movie> allMovies = this.movieService.findAllMovies();
        ListOfMoviesDto listOfMovies = this.movieService.mapMoviesToDto(allMovies);
        model.addAttribute("moviesData", listOfMovies);
        return "Movies";
    }

    @PostMapping("Review/{reviewId}/{movieId}/like")
    public String likeHomeReview(@PathVariable("reviewId") Long reviewId, @PathVariable("movieId") Long movieId) {
        this.reviewClient.likeReview(reviewId,userHelperService.getUser().getId());
        return "redirect:/Movie/" + movieId;
    }

    @PostMapping("Review/{reviewId}/{movieId}/dislike")
    public String dislikeHomeReview(@PathVariable("reviewId") Long reviewId, @PathVariable("movieId") Long movieId) {
        this.reviewClient.dislikeReview(reviewId,userHelperService.getUser().getId());
        return "redirect:/Movie/" + movieId;
    }
}
