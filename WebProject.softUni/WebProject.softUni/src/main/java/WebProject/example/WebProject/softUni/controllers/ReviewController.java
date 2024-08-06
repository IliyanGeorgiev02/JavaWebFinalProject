package WebProject.example.WebProject.softUni.controllers;

import WebProject.example.WebProject.softUni.dtos.AddReviewDto;
import WebProject.example.WebProject.softUni.dtos.EditReviewDto;
import WebProject.example.WebProject.softUni.dtos.MovieFullInfoDto;
import WebProject.example.WebProject.softUni.model.Comment;
import WebProject.example.WebProject.softUni.model.Movie;
import WebProject.example.WebProject.softUni.model.Review;
import WebProject.example.WebProject.softUni.services.*;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
public class ReviewController {
    private final OmdbService omdbService;
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);
    private final ReviewService reviewService;
    private final UserService userService;
    private final MovieService movieService;
    private final ModelMapper modelMapper;
    private final UserHelperService userHelperService;
    private final CommentsService commentsService;

    public ReviewController(OmdbService omdbService, ReviewService reviewService, UserService userService, MovieService movieService, ModelMapper modelMapper, UserHelperService userHelperService, CommentsService commentsService) {
        this.omdbService = omdbService;
        this.reviewService = reviewService;
        this.userService = userService;
        this.movieService = movieService;
        this.modelMapper = modelMapper;
        this.userHelperService = userHelperService;
        this.commentsService = commentsService;
    }

    @PostMapping("/AddReview")
    public String getAddReview(@RequestParam("title") String title,
                               @RequestParam("year") String year, Model model) {
        AddReviewDto addReviewDto = new AddReviewDto();
        addReviewDto.setMovieTitle(title);
        addReviewDto.setMovieYear(year);
        model.addAttribute("reviewDetails", addReviewDto);
        return "AddReview";
    }


    @PostMapping("/Review")
    public String postReview(@Valid @ModelAttribute("reviewDetails") AddReviewDto addReviewDto, Model model, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("registerUserDto", addReviewDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addReviewDto", bindingResult);
        }
        MovieFullInfoDto movieFullInfoDto = this.omdbService.searchByTitleAndYear(addReviewDto.getMovieTitle(), addReviewDto.getMovieYear());
        Movie mappedMovie;
        mappedMovie = this.movieService.mapMovie(movieFullInfoDto);
        Optional<Movie> movie = this.movieService.findMovie(mappedMovie);
        if (movie.isEmpty()) {
            this.movieService.saveMovie(mappedMovie);
        }
        Review review = this.reviewService.mapReview(addReviewDto, mappedMovie);
        this.reviewService.saveReview(review);
        model.addAttribute("reviewData", review);
        return "redirect:/Review/" + review.getId();
    }


    @GetMapping("/Review/{id}")
    public String getReview(@PathVariable("id") Long reviewId, Model model) {
        Optional<Review> review = this.reviewService.findReviewById(reviewId);
        if (review.isPresent()) {
            model.addAttribute("reviewData", review.get());
            return "Review";
        } else {
            return "redirect:/error";
        }
    }

    @DeleteMapping("/Review/{id}")
    public String deleteReview(@PathVariable("id") Long reviewId, Model model) {
        Optional<Review> review = this.reviewService.findReviewById(reviewId);
        if (review.isPresent()) {
            Review presentReview = review.get();
            this.reviewService.deleteReview(presentReview);
        }
        return "redirect:/Reviews";
    }


    @GetMapping("/EditReview/{id}")
    public String getEditReview(@PathVariable("id") Long reviewId, Model model) {
        Optional<Review> review = this.reviewService.findReviewById(reviewId);
        if (review.isPresent()) {
            model.addAttribute("reviewData", review.get());
            model.addAttribute("newReviewData", new EditReviewDto());
            return "EditReview";
        }
        model.addAttribute("reviewNotFound");
        return "redirect:/EditReview";
    }

    @PutMapping("/EditReview/{id}")
    public String editReview(@PathVariable("id") Long reviewId,
                             @Valid @ModelAttribute("newReviewData") EditReviewDto newReviewData,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        Review existingReview = reviewService.findReviewById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("newReviewData", newReviewData);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.newReviewData", bindingResult);
            return "redirect:/Review/" + reviewId;
        }
        reviewService.updateReview(newReviewData, existingReview);
        return "redirect:/Review/" + reviewId;
    }

    @GetMapping("/Reviews")
    public String getReviews(Model model) {
        List<Review> allReviews = this.reviewService.findALLReviews();
        model.addAttribute("ListData", allReviews);
        return "redirect:/Reviews";
    }

    @GetMapping("/Reviews/{username}")
    public String getReviewsByUsername(@PathVariable("username") String username, Model model) {
        List<Review> allReviewsByUsername = this.userService.findAllReviewsByUser(username);
        model.addAttribute("ListData", allReviewsByUsername);
        return "Reviews";
    }
}
