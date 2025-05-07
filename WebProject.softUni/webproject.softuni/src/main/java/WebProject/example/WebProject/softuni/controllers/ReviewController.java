package webproject.example.webproject.softuni.controllers;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import webproject.example.webproject.softuni.clients.ReviewClient;
import webproject.example.webproject.softuni.dtos.*;
import webproject.example.webproject.softuni.model.*;
import webproject.example.webproject.softuni.model.enums.UserRoleEnum;
import webproject.example.webproject.softuni.services.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Controller
public class ReviewController {
    private final OmdbService omdbService;
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);
    private final MovieService movieService;
    private final UserHelperService userHelperService;
    private final CommentsService commentsService;
    private final ReviewClient reviewClient;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final MovieController movieController;

    public ReviewController(OmdbService omdbService, MovieService movieService, UserHelperService userHelperService, CommentsService commentsService, ReviewClient reviewClient, UserService userService, ModelMapper modelMapper, MovieController movieController) {
        this.omdbService = omdbService;
        this.movieService = movieService;
        this.userHelperService = userHelperService;
        this.commentsService = commentsService;
        this.reviewClient = reviewClient;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.movieController = movieController;
    }

    @PostMapping("/AddReview")
    public String postAddReview(@RequestParam("title") String title,
                                @RequestParam("year") String year,
                                HttpSession httpSession,
                                RedirectAttributes redirectAttributes) {


        User user = this.userHelperService.getUser();

        List<ReviewFullInfoDto> userReviews = reviewClient.getReviewsByUserId(user.getId());
        boolean hasReviewed = userReviews.stream()
                .anyMatch(review -> review.getMovieTitle().equals(title) && Year.parse(review.getMovieRelease().substring(0, 4)).equals(Year.parse(year)));

        if (hasReviewed) {
            redirectAttributes.addFlashAttribute("errorMessage", "You have already reviewed this movie.");
            return "redirect:/ListOfMovies";
        }

        AddReviewDto addReviewDto = new AddReviewDto();
        addReviewDto.setMovieTitle(title);
        addReviewDto.setMovieYear(year);
        addReviewDto.setUserId(user.getId());
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

        // Step 1: Find or map the movie from external service
        MovieFullInfoDto movieFullInfoDto = this.omdbService.searchByTitleAndYear(addReviewDto.getMovieTitle(), addReviewDto.getMovieYear());
        Movie mappedMovie = this.movieService.mapMovie(movieFullInfoDto);

        Optional<Movie> existingMovie = this.movieService.findMovie(mappedMovie);

        if (existingMovie.isEmpty()) {
            this.movieService.saveMovie(mappedMovie);
            existingMovie = Optional.of(mappedMovie);
        }

        Movie movieToUse = existingMovie.get();
        addReviewDto.setUserId(this.userHelperService.getUser().getId());
        ReviewFullInfoDto reviewFullInfoDto = this.modelMapper.map(addReviewDto, ReviewFullInfoDto.class);
        reviewFullInfoDto.setMovieRelease(String.valueOf(movieToUse.getReleased()));
        reviewFullInfoDto.setPosterUrl(movieToUse.getPosterUrl());
        reviewFullInfoDto.setDirector(movieToUse.getDirector());
        reviewFullInfoDto.setUsername(this.userHelperService.getUser().getUsername());
        reviewFullInfoDto.setMovieId(movieToUse.getId());
        // Step 2: Create the Review using the ReviewClient (instead of directly using reviewService)
        try {
            ReviewFullInfoDto createdReview = this.reviewClient.createReview(reviewFullInfoDto);  // This sends the review to the Review API
            movieToUse.getReviewsIds().add(createdReview.getId());
            this.movieService.saveMovie(movieToUse);
            this.userHelperService.getUser().getReview_ids().add(createdReview.getId());
            this.userService.saveUser(userHelperService.getUser());
            // Step 3: Redirect to the newly created review's page using the ID from the response
            redirectAttributes.addFlashAttribute("reviewData", createdReview);
            return "redirect:/Review/" + createdReview.getId();

        } catch (Exception e) {
            // Step 4: Handle errors appropriately
            redirectAttributes.addFlashAttribute("message", "An error occurred while posting the review: " + e.getMessage());
            return "redirect:/AddReview";
        }
    }


    @Transactional
    @GetMapping("/Review/{id}")
    public String getReview(@PathVariable("id") Long reviewId, Model model) {
        Optional<ReviewFullInfoDto> reviewById = this.reviewClient.getReviewById(reviewId);
        User user = this.userHelperService.getUser();
        if (reviewById.isPresent()) {
            ReviewFullInfoDto reviewFullInfoDto = reviewById.get();
            boolean isAdmin = user.getRole().equals(UserRoleEnum.ADMIN);
            List<Comment> reviewComments = this.commentsService.findByReviewId(reviewFullInfoDto.getId());
            ListOfCommentsDto commentsData = this.commentsService.mapCommentsToListOfCommentsDto(reviewComments);
            model.addAttribute("reviewData", reviewFullInfoDto);
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
        this.reviewClient.deleteReview(id);
        return "redirect:/Reviews";
    }

    @GetMapping("/EditReview/{id}")
    public String getEditReview(@PathVariable("id") Long reviewId, Model model) {
        Optional<ReviewFullInfoDto> review = this.reviewClient.getReviewById(reviewId);
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
        Optional<ReviewFullInfoDto> existingReview = reviewClient.getReviewById(reviewId);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("newReviewData", newReviewData);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.newReviewData", bindingResult);
            return "redirect:/EditReview/" + reviewId;
        }
        if (existingReview.isPresent()) {
            ReviewFullInfoDto reviewFullInfoDto = existingReview.get();
            reviewClient.updateReview(newReviewData, reviewFullInfoDto);
        }
        return "redirect:/Review/" + reviewId;
    }


    @Transactional
    @GetMapping("/Reviews")
    public String getReviews(Model model) {
        List<ReviewFullInfoDto> allReviews = this.reviewClient.getALLReviews();
        ReviewListDto reviewListDto = this.reviewClient.mapReviewsToDto(allReviews);
        model.addAttribute("ReviewsData", reviewListDto);
        return "Reviews";
    }

    @Transactional
    @GetMapping("/Reviews/{username}")
    public String getReviewsByUsername(@PathVariable("username") String username, Model model) {
        Optional<User> userByUsername = this.userService.findUserByUsername(username);
        User user = userByUsername.get();
        List<ReviewFullInfoDto> allReviewsByUsername = this.reviewClient.getReviewsByUserId(user.getId());
        ReviewListDto reviewListDto = this.reviewClient.mapReviewsToDto(allReviewsByUsername);
        model.addAttribute("ReviewsData", reviewListDto);
        return "Reviews";
    }

    @Transactional
    @PostMapping("Review/{reviewId}/like")
    public String likeReview(@PathVariable("reviewId") Long reviewId) {
        this.reviewClient.likeReview(reviewId, userHelperService.getUser().getId());
        return "redirect:/Review/" + reviewId;
    }

    @Transactional
    @PostMapping("Review/{reviewId}/dislike")
    public String dislikeReview(@PathVariable("reviewId") Long reviewId) {
        this.reviewClient.dislikeReview(reviewId, userHelperService.getUser().getId());
        return "redirect:/Review/" + reviewId;
    }

    @Transactional
    @PostMapping("Home/Review/{reviewId}/like")
    public String likeHomeReview(@PathVariable("reviewId") Long reviewId) {
        this.reviewClient.likeReview(reviewId, userHelperService.getUser().getId());
        return "redirect:/home";
    }

    @Transactional
    @PostMapping("Home/Review/{reviewId}/dislike")
    public String dislikeHomeReview(@PathVariable("reviewId") Long reviewId) {
        this.reviewClient.dislikeReview(reviewId, userHelperService.getUser().getId());
        return "redirect:/home";
    }
}
