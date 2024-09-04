package webproject.example.webproject.softuni.controllers;

import jakarta.transaction.Transactional;
import webproject.example.webproject.softuni.dtos.ListDto;
import webproject.example.webproject.softuni.dtos.ListOfMoviesDto;
import webproject.example.webproject.softuni.dtos.MovieFullInfoDto;
import webproject.example.webproject.softuni.dtos.ReviewListDto;
import webproject.example.webproject.softuni.model.CustomList;
import webproject.example.webproject.softuni.model.Movie;
import webproject.example.webproject.softuni.model.Review;
import webproject.example.webproject.softuni.services.ListService;
import webproject.example.webproject.softuni.services.MovieService;
import webproject.example.webproject.softuni.services.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;

@Controller
public class HomeController {
    private final MovieService movieService;
    private final ListService listService;
    private final ReviewService reviewService;

    public HomeController(MovieService movieService, ListService listService, ReviewService reviewService) {
        this.movieService = movieService;
        this.listService = listService;
        this.reviewService = reviewService;
    }
    @Transactional
    @GetMapping("/home")
    public String getHome(Model model) {
        List<CustomList> allLists = listService.findAllLists();
        List<CustomList> sortedLists = allLists.stream()
                .sorted(Comparator.comparing(CustomList::getLikesCount))
                .limit(4)
                .toList();

        ListDto listDto = this.listService.mapCustomListsToListDto(sortedLists);
        model.addAttribute("listData", listDto);

        List<Review> allReviews = reviewService.findALLReviews();
        List<Review> sortedReviews = allReviews.stream()
                .sorted(Comparator.comparing(Review::getLikesCount).reversed())
                .limit(4)
                .toList();
        ReviewListDto reviewListDto = this.reviewService.mapReviewsToDto(sortedReviews);
        model.addAttribute("reviewsData", reviewListDto);
        List<Movie> allMovies = this.movieService.findAllMovies();
        allMovies.sort(Comparator.comparingInt((Movie m) -> m.getReviews().size())
                .thenComparingInt(m -> m.getCustomLists().size())
                .reversed());
        ListOfMoviesDto listOfMoviesDto = this.movieService.mapMoviesToDto(allMovies);
        model.addAttribute("moviesData", listOfMoviesDto);
        Movie randomMovie = movieService.getSelectedMovie();
        if (randomMovie != null) {
            MovieFullInfoDto recommended = this.movieService.mapMovieShortInfo(randomMovie);
            model.addAttribute("recommendation", recommended);
        } else {
            model.addAttribute("recommendation", null);
        }
        return "home";
    }
}
