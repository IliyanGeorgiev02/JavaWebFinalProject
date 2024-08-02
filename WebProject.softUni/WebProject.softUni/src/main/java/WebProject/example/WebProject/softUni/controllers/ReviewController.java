package WebProject.example.WebProject.softUni.controllers;

import WebProject.example.WebProject.softUni.dtos.MovieReviewInfoDto;
import WebProject.example.WebProject.softUni.model.Review;
import WebProject.example.WebProject.softUni.services.OmdbService;
import WebProject.example.WebProject.softUni.services.ReviewService;
import WebProject.example.WebProject.softUni.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ReviewController {
    private final OmdbService omdbService;
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);
    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(OmdbService omdbService, ReviewService reviewService, UserService userService) {
        this.omdbService = omdbService;
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @PostMapping("/AddReview")
    public String addReview(@RequestParam("title") String title,
                            @RequestParam("year") String year,
                            Model model) {
        MovieReviewInfoDto movieResponseDto = new MovieReviewInfoDto();
        movieResponseDto.setMovieTitle(title);
        movieResponseDto.setReleaseYear(year);
        model.addAttribute("movieDetails", movieResponseDto);
        return "AddReview";
    }

    @GetMapping("/Reviews")
    public String getReviews(Model model) {
        List<Review> allReviews = this.reviewService.findALLReviews();
        model.addAttribute("ListData", allReviews);
        return "Reviews";
    }

    @GetMapping("/Reviews/{username}")
    public String getReviewsByUsername(@PathVariable("username")String username, Model model) {
        List<Review> allReviewsByUsername = this.userService.findAllReviewsByUser(username);
        model.addAttribute("ListData", allReviewsByUsername);
        return "Reviews";
    }

}
