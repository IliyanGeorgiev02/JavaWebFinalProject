package webproject.example.webproject.softuni.services;

import org.springframework.security.core.parameters.P;
import webproject.example.webproject.softuni.dtos.*;
import webproject.example.webproject.softuni.model.Like;
import webproject.example.webproject.softuni.model.Movie;
import webproject.example.webproject.softuni.model.Review;
import webproject.example.webproject.softuni.model.User;
import webproject.example.webproject.softuni.repositories.LikeRepository;
import webproject.example.webproject.softuni.repositories.ReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserHelperService userHelperService;
    private final ModelMapper modelMapper;
    private final LikeRepository likeRepository;
    private final LikeService likeService;
    public ReviewService(ReviewRepository reviewRepository, UserHelperService userHelperService, ModelMapper modelMapper, LikeRepository likeRepository, LikeService likeService) {
        this.reviewRepository = reviewRepository;
        this.userHelperService = userHelperService;
        this.modelMapper = modelMapper;
        this.likeRepository = likeRepository;
        this.likeService = likeService;
    }

    public List<Review> findALLReviews() {
        return this.reviewRepository.findAll();
    }

    public void saveReview(Review review) {
        this.reviewRepository.save(review);
    }

    public Optional<Review> findReviewById(Long reviewId) {
        return this.reviewRepository.findById(reviewId);
    }

    public Review mapReview(AddReviewDto addReviewDto, Movie mappedMovie) {
        Review review = new Review();
        review.setReviewTitle(addReviewDto.getReviewTitle());
        review.setReviewText(addReviewDto.getReviewText());
        review.setRating(addReviewDto.getReviewRating());
        Like like=new Like();
        this.likeRepository.save(like);
        review.setLikes(new HashSet<>());
        review.setMovie(mappedMovie);
        review.setUser(this.userHelperService.getUser());
        review.setCreated(LocalDate.now());
        return review;
    }

    public void deleteReview(Review review) {
        this.reviewRepository.delete(review);
    }

    public void updateReview(EditReviewDto newReviewData, Review review) {
        updateFieldIfNotBlank(newReviewData::getReviewTitle, review::setReviewTitle);
        updateFieldIfValidRating(newReviewData.getReviewRating(), review::setRating);
        updateFieldIfNotBlank(newReviewData::getReviewText, review::setReviewText);
        review.setEdited(LocalDate.now());
        reviewRepository.save(review);
    }

    private void updateFieldIfNotBlank(Supplier<String> newValueSupplier, Consumer<String> updateFunction) {
        String newValue = newValueSupplier.get();
        if (!newValue.isBlank()) {
            updateFunction.accept(newValue);
        }
    }

    private void updateFieldIfValidRating(int newRating, Consumer<Integer> updateFunction) {
        if (newRating > 0) {
            updateFunction.accept(newRating);
        }
    }

    public void likeReview(Long reviewId, User user) {
        Optional<Review> byId = this.reviewRepository.findById(reviewId);
        if (byId.isPresent()) {
            Review review = byId.get();
            boolean alreadyLiked = review.getLikes().stream()
                    .anyMatch(like -> like.getLiked().equals(user));
            if (!alreadyLiked) {
                Like like = new Like();
                like.setLiked(user);
                like.setReview(review);
                review.getLikes().add(like);
                this.reviewRepository.save(review);
            }
        }
    }


    public void dislikeReview(Long reviewId, User user) {
        Optional<Review> byId = this.reviewRepository.findById(reviewId);
        if (byId.isPresent()) {
            Review review = byId.get();
            Optional<Like> likeToRemove = review.getLikes().stream()
                    .filter(like -> like.getLiked().equals(user))
                    .findFirst();
            if (likeToRemove.isPresent()) {
                review.getLikes().remove(likeToRemove.get());
                this.reviewRepository.save(review);
            }
        }
    }


    public List<Review> findALLReviewsByUser(long id) {
        return this.reviewRepository.findByUserId(id);
    }

    public DisplayReviewDto mapReviewsToDisplayReviewDto(List<Review> reviews) {
        List<MovieReviewInfoDto> movieReviewInfoDtos = reviews.stream()
                .map(this::convertToMovieReviewInfoDto)
                .collect(Collectors.toList());
        DisplayReviewDto displayReviewDto = new DisplayReviewDto();
        displayReviewDto.setReviews(movieReviewInfoDtos);
        return displayReviewDto;
    }

    private MovieReviewInfoDto convertToMovieReviewInfoDto(Review review) {
        MovieReviewInfoDto movieReviewInfoDto = new MovieReviewInfoDto();
        Movie movie = review.getMovie();
        if (movie != null) {
            movieReviewInfoDto.setMovieTitle(movie.getTitle());
            movieReviewInfoDto.setDirector(movie.getDirector());
            movieReviewInfoDto.setPosterUrl(movie.getPosterUrl());
        }
        movieReviewInfoDto.setReviewTitle(review.getReviewTitle());
        movieReviewInfoDto.setReviewRating(String.valueOf(review.getRating()));
        return movieReviewInfoDto;
    }

    public ReviewFullInfoDto mapReviewData(Review reviewData) {
        ReviewFullInfoDto mappedReview = this.modelMapper.map(reviewData, ReviewFullInfoDto.class);
        mappedReview.setUsername(reviewData.getUser().getUsername());
        mappedReview.setId(reviewData.getId());
        mappedReview.setMovieTitle(reviewData.getMovie().getTitle());
        mappedReview.setMovieRelease(reviewData.getMovie().getYear().toString());
        mappedReview.setPosterUrl(reviewData.getMovie().getPosterUrl());
        mappedReview.setUserId(reviewData.getUser().getId());
        return mappedReview;
    }

    public ReviewListDto mapReviewsToDto(List<Review> reviews) {
        List<ReviewFullInfoDto> reviewDtos = reviews.stream()
                .map(this::mapReviewData)
                .collect(Collectors.toList());

        ReviewListDto reviewListDto = new ReviewListDto();
        reviewListDto.setReviews(reviewDtos);
        return reviewListDto;
    }

    public List<Review> findALLReviewsByMovieId(long id) {
        return this.reviewRepository.findByMovieId(id);
    }

    public Optional<User> findUserByReviewId(Long reviewId) {
        return this.reviewRepository.findUserByReviewId(reviewId);
    }
}
