package webproject.example.webproject.softuni.controllers;

import jakarta.transaction.Transactional;
import webproject.example.webproject.softuni.clients.ReviewClient;
import webproject.example.webproject.softuni.dtos.*;
import webproject.example.webproject.softuni.model.CustomList;
import webproject.example.webproject.softuni.model.Movie;
import webproject.example.webproject.softuni.services.ListService;
import webproject.example.webproject.softuni.services.MovieService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;

@Controller
public class HomeController {
    private final MovieService movieService;
    private final ListService listService;
    private final ReviewClient reviewClient;

    public HomeController(MovieService movieService, ListService listService, ReviewClient reviewClient) {
        this.movieService = movieService;
        this.listService = listService;
        this.reviewClient = reviewClient;
    }

    @Transactional
    @GetMapping("/home")
    public String getHome(Model model) {
        List<CustomList> allLists = listService.findAllLists();
        List<CustomList> sortedLists = allLists.stream()
                .sorted(Comparator.comparing(CustomList::getLikesCount).reversed())
                .limit(4)
                .toList();
        ListDto listDto = this.listService.mapCustomListsToListDto(sortedLists);
        model.addAttribute("listData", listDto);

        List<ReviewFullInfoDto> allReviews = reviewClient.getALLReviews();
        List<ReviewFullInfoDto> sortedReviews = allReviews.stream()
                .sorted(Comparator.comparing(ReviewFullInfoDto::getLikes).reversed())
                .limit(4)
                .toList();
        ReviewListDto reviewListDto = this.reviewClient.mapReviewsToDto(sortedReviews);
        model.addAttribute("reviewsData", reviewListDto);

        List<Movie> allMovies = this.movieService.findAllMovies();
        allMovies.sort(Comparator.comparingInt((Movie m) -> m.getReviewsIds().size())
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
