package WebProject.example.WebProject.softUni.controllers;

import WebProject.example.WebProject.softUni.dtos.ListDto;
import WebProject.example.WebProject.softUni.dtos.ListOfMoviesDto;
import WebProject.example.WebProject.softUni.dtos.ReviewListDto;
import WebProject.example.WebProject.softUni.model.CustomList;
import WebProject.example.WebProject.softUni.model.Movie;
import WebProject.example.WebProject.softUni.model.Review;
import WebProject.example.WebProject.softUni.services.ListService;
import WebProject.example.WebProject.softUni.services.MovieService;
import WebProject.example.WebProject.softUni.services.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/home")
    public String getHome(Model model) {
        List<CustomList> allLists = listService.findAllLists();
        List<CustomList> sortedLists = allLists.stream()
                .sorted(Comparator.comparing(CustomList::getLikes).reversed())
                .limit(4)
                .toList();
        ListDto listDto = this.listService.mapCustomListsToListDto(sortedLists);
        model.addAttribute("listsData", listDto);

        List<Review> allReviews = reviewService.findALLReviews();
        List<Review> sortedReviews = allReviews.stream()
                .sorted(Comparator.comparing(Review::getLikes).reversed())
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
        return "home";
    }
}
