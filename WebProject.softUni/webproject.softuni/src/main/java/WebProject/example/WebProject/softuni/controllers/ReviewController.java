package webproject.example.webproject.softuni.controllers;

import jakarta.transaction.Transactional;
import webproject.example.webproject.softuni.dtos.*;
import webproject.example.webproject.softuni.model.*;
import webproject.example.webproject.softuni.model.enums.UserRoleEnum;
import webproject.example.webproject.softuni.services.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Year;
import java.util.HashSet;
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
    public String postAddReview(@RequestParam("title") String title,
                                @RequestParam("year") String year,
                                HttpSession httpSession,
                                RedirectAttributes redirectAttributes) {
        Optional<Movie> movieOpt = this.movieService.findMovieByTitleAndYear(title, Year.parse(year));

        if (movieOpt.isPresent()) {
            Movie movie = movieOpt.get();
            User user = this.userHelperService.getUser();
            boolean hasReviewed = user.getReviews().stream()
                    .anyMatch(review -> review.getMovie().getImdbId().equals(movie.getImdbId()));

            if (hasReviewed) {
                redirectAttributes.addFlashAttribute("errorMessage", "You have already reviewed this movie.");
                return "redirect:/ListOfMovies";
            }
        }
        AddReviewDto addReviewDto = new AddReviewDto();
        addReviewDto.setMovieTitle(title);
        addReviewDto.setMovieYear(year);
        httpSession.setAttribute("reviewDetails", addReviewDto);

        return "redirect:/AddReview";
    }


    @GetMapping("/AddReview")
    public String getAddReview(Model model, HttpSession httpSession) {
        AddReviewDto addReviewDto = (AddReviewDto) httpSession.getAttribute("reviewDetails");
        if (addReviewDto == null) {
            addReviewDto = new AddReviewDto();
            addReviewDto.setMovieYear("");
            addReviewDto.setMovieTitle("");
            model.addAttribute("message", "You have not selected a movie to review.");
        }
        if (!model.containsAttribute("reviewDetails")) {
            model.addAttribute("reviewDetails", addReviewDto);
        }
        return "AddReview";
    }

    @PostMapping("/Review")
    public String postReview(@Valid @ModelAttribute("reviewDetails") AddReviewDto addReviewDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("reviewDetails", addReviewDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.reviewDetails", bindingResult);
            redirectAttributes.addFlashAttribute("message", "Cannot post a review without selecting a movie first.");
            return "redirect:/AddReview";
        }

        MovieFullInfoDto movieFullInfoDto = this.omdbService.searchByTitleAndYear(addReviewDto.getMovieTitle(), addReviewDto.getMovieYear());
        Movie mappedMovie = this.movieService.mapMovie(movieFullInfoDto);

        Optional<Movie> existingMovie = this.movieService.findMovie(mappedMovie);

        if (existingMovie.isEmpty()) {
            this.movieService.saveMovie(mappedMovie);
            existingMovie = Optional.of(mappedMovie);
        }

        Movie movieToUse = existingMovie.get();

        Review review = this.reviewService.mapReview(addReviewDto, movieToUse);
        this.reviewService.saveReview(review);

        redirectAttributes.addFlashAttribute("reviewData", review);
        return "redirect:/Review/" + review.getId();
    }

    @Transactional
    @GetMapping("/Review/{id}")
    public String getReview(@PathVariable("id") Long reviewId, Model model) {
        Optional<Review> review = this.reviewService.findReviewById(reviewId);
        User user = this.userHelperService.getUser();
        if (review.isPresent()) {
            Review reviewData = review.get();
            boolean isAdmin = user.getRole().equals(UserRoleEnum.ADMIN);
            ReviewFullInfoDto mappedReview = this.reviewService.mapReviewData(reviewData);
            List<Comment> reviewComments = this.commentsService.findByReviewId(reviewData.getId());
            ListOfCommentsDto commentsData = this.commentsService.mapCommentsToListOfCommentsDto(reviewComments);
            model.addAttribute("reviewData", mappedReview);
            model.addAttribute("userHasRoleAdmin", isAdmin);
            model.addAttribute("addCommentDto", new AddCommentDto());
            model.addAttribute("commentsData", commentsData);
            model.addAttribute("currentUsername", userHelperService.getUserDetails().getUsername());
            return "Review";
        } else {
            model.addAttribute("reviewData", null);
            return "Review";
        }
    }

    @DeleteMapping("/Review/{id}")
    public String deleteReview(@PathVariable Long id) {
        Optional<Review> review = this.reviewService.findReviewById(id);
        review.ifPresent(reviewService::deleteReview);
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

    @Transactional
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

    @Transactional
    @PostMapping("Review/{reviewId}/like")
    public String likeReview(@PathVariable("reviewId") Long reviewId) {
        this.reviewService.likeReview(reviewId,userHelperService.getUser());
        return "redirect:/Review/" + reviewId;
    }

    @Transactional
    @PostMapping("Review/{reviewId}/dislike")
    public String dislikeReview(@PathVariable("reviewId") Long reviewId) {
        this.reviewService.dislikeReview(reviewId,userHelperService.getUser());
        return "redirect:/Review/" + reviewId;
    }

    @Transactional
    @PostMapping("Home/Review/{reviewId}/like")
    public String likeHomeReview(@PathVariable("reviewId") Long reviewId) {
        this.reviewService.likeReview(reviewId,userHelperService.getUser());
        return "redirect:/home";
    }
    @Transactional
    @PostMapping("Home/Review/{reviewId}/dislike")
    public String dislikeHomeReview(@PathVariable("reviewId") Long reviewId) {
        this.reviewService.dislikeReview(reviewId,userHelperService.getUser());
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
        mappedComment.setLikes(new HashSet<>());
        mappedComment.setReview(review);
        mappedComment.setUser(userHelperService.getUser());
        this.commentsService.addComment(mappedComment);
        return "redirect:/Review/" + id;
    }
}
