package WebProject.example.WebProject.softUni.controllers;

import WebProject.example.WebProject.softUni.dtos.*;
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

import java.util.List;
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
            Review reviewData = review.get();
            ReviewFullInfoDto mappedReview = this.reviewService.mapReviewData(reviewData);
            List<Comment> reviewComments = this.commentsService.findByReviewId(reviewData.getId());
            ListOfCommentsDto commentsData = this.commentsService.mapCommentsToListOfCommentsDto(reviewComments);
            model.addAttribute("reviewData", mappedReview);
            model.addAttribute("addCommentDto", new AddCommentDto());
            model.addAttribute("commentsData", commentsData);
            return "Review";
        } else {
            model.addAttribute("reviewData", null);
            return "Review";
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
        model.addAttribute("reviewData", null);
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
            return "redirect:/EditReview/" + reviewId;
        }
        reviewService.updateReview(newReviewData, existingReview);
        return "redirect:/Review/" + reviewId;
    }

    @GetMapping("/Reviews")
    public String getReviews(Model model) {
        List<Review> allReviews = this.reviewService.findALLReviews();
        ReviewListDto reviewListDto = this.reviewService.mapReviewsToDto(allReviews);
        model.addAttribute("ReviewsData", reviewListDto);
        return "Reviews";
    }

    @GetMapping("/Reviews/{username}")
    public String getReviewsByUsername(@PathVariable("username") String username, Model model) {
        List<Review> allReviewsByUsername = this.userService.findAllReviewsByUser(username);
        ReviewListDto reviewListDto = this.reviewService.mapReviewsToDto(allReviewsByUsername);
        model.addAttribute("ReviewsData", reviewListDto);
        return "Reviews";
    }

    @PostMapping("Review/Comments/{commentId}/{reviewId}/like")
    public String likeReviewComment(@PathVariable("reviewId") Long reviewId, @PathVariable("commentId") Long commentId) {
        this.commentsService.likeComment(commentId);
        return "redirect:/Review/" + reviewId;
    }

    @PostMapping("Review/Comments/{commentId}/{reviewId}/dislike")
    public String dislikeReviewComment(@PathVariable("reviewId") Long reviewId, @PathVariable("commentId") Long commentId) {
        this.commentsService.dislikeComment(commentId);
        return "redirect:/Review/" + reviewId;
    }

    @PostMapping("Review/{reviewId}/like")
    public String likeReview(@PathVariable("reviewId") Long reviewId) {
        this.reviewService.likeReview(reviewId);
        return "redirect:/Review/" + reviewId;
    }

    @PostMapping("Review/{reviewId}/dislike")
    public String dislikeReview(@PathVariable("reviewId") Long reviewId) {
        this.reviewService.dislikeReview(reviewId);
        return "redirect:/Review/" + reviewId;
    }

    @PostMapping("Home/Review/{reviewId}/like")
    public String likeHomeReview(@PathVariable("reviewId") Long reviewId) {
        this.reviewService.likeReview(reviewId);
        return "redirect:/home";
    }

    @PostMapping("Home/Review/{reviewId}/dislike")
    public String dislikeHomeReview(@PathVariable("reviewId") Long reviewId) {
        this.reviewService.dislikeReview(reviewId);
        return "redirect:/home";
    }

    @PostMapping("/Review/{reviewId}")
    public String postComment(@ModelAttribute("addCommentDto") AddCommentDto addCommentDto, @PathVariable("reviewId") long id) {
        Optional<Review> reviewById = this.reviewService.findReviewById(id);
        if (reviewById.isEmpty()) {
            return "redirect:/home";
        }
        Review review = reviewById.get();
        Comment mappedComment = this.modelMapper.map(addCommentDto, Comment.class);
        mappedComment.setLikes(0);
        mappedComment.setReview(review);
        mappedComment.setUser(userHelperService.getUser());
        this.commentsService.addComment(mappedComment);
        return "redirect:/Review/" + id;
    }
}
