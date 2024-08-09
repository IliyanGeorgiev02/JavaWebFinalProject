package WebProject.example.WebProject.softUni.controllers;

import WebProject.example.WebProject.softUni.dtos.ListOfMoviesDto;
import WebProject.example.WebProject.softUni.dtos.MovieFullInfoDto;
import WebProject.example.WebProject.softUni.dtos.ReviewListDto;
import WebProject.example.WebProject.softUni.model.Movie;
import WebProject.example.WebProject.softUni.model.Review;
import WebProject.example.WebProject.softUni.services.MovieService;
import WebProject.example.WebProject.softUni.services.ReviewService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class MovieController {
    private final MovieService movieService;
    private final ReviewService reviewService;
    private final ModelMapper modelMapper;

    public MovieController(MovieService movieService, ReviewService reviewService, ModelMapper modelMapper) {
        this.movieService = movieService;
        this.reviewService = reviewService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/Movie/{id}")
    public String getMovie(@PathVariable("id") long id, Model model) {
        Optional<Movie> movieById = this.movieService.findMovieById(id);
        if (movieById.isPresent()) {
            Movie movie = movieById.get();
            MovieFullInfoDto mappedMovie = this.movieService.mapMovieShortInfo(movie);
            List<Review> allReviewsByMovieId = this.reviewService.findALLReviewsByMovieId(id);
            ReviewListDto reviewsData = this.reviewService.mapReviewsToDto(allReviewsByMovieId);
            model.addAttribute("movieData", mappedMovie);
            model.addAttribute("reviewsData", reviewsData);
            return "Movie";
        }
        return "Home";
    }

    @GetMapping("/Movies")
    public String getMoviesPage(Model model) {
        List<Movie> allMovies = this.movieService.findAllMovies();
        ListOfMoviesDto listOfMovies=this.movieService.mapMoviesToDto(allMovies);
        model.addAttribute("moviesData", listOfMovies);
        return "Movies";
    }

    @PostMapping("Review/{reviewId}/{movieId}/like")
    public String likeHomeReview(@PathVariable("reviewId") Long reviewId, @PathVariable("movieId") Long movieId) {
        this.reviewService.likeReview(reviewId);
        return "redirect:/Movie/" + movieId;
    }

    @PostMapping("Review/{reviewId}/{movieId}/dislike")
    public String dislikeHomeReview(@PathVariable("reviewId") Long reviewId, @PathVariable("movieId") Long movieId) {
        this.reviewService.dislikeReview(reviewId);
        return "redirect:/Movie/" + movieId;
    }
}
