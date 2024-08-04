package WebProject.example.WebProject.softUni.controllers;

import WebProject.example.WebProject.softUni.dtos.AddReviewDto;
import WebProject.example.WebProject.softUni.dtos.MovieFullInfoDto;
import WebProject.example.WebProject.softUni.dtos.MovieResponseDto;
import WebProject.example.WebProject.softUni.dtos.MovieReviewInfoDto;
import WebProject.example.WebProject.softUni.model.Review;
import WebProject.example.WebProject.softUni.services.OmdbService;
import WebProject.example.WebProject.softUni.services.ReviewService;
import WebProject.example.WebProject.softUni.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String addReview(@ModelAttribute("movieData") MovieFullInfoDto movieInfo, Model model) {
        MovieFullInfoDto movieFullInfo = this.omdbService.searchByTitleAndYear(movieInfo.getTitle(), movieInfo.getYear());
        AddReviewDto addReviewDto = new AddReviewDto();
        addReviewDto.setMovie(movieFullInfo);
        model.addAttribute("reviewDetails", addReviewDto);
        return "AddReview";
    }


    @GetMapping("/Reviews")
    public String getReviews(Model model) {
        List<Review> allReviews = this.reviewService.findALLReviews();
        model.addAttribute("ListData", allReviews);
        return "Reviews";
    }

    @GetMapping("/Reviews/{username}")
    public String getReviewsByUsername(@PathVariable("username") String username, Model model) {
        List<Review> allReviewsByUsername = this.userService.findAllReviewsByUser(username);
        model.addAttribute("ListData", allReviewsByUsername);
        return "Reviews";
    }

}
