package WebProject.example.WebProject.softUni.services;

import WebProject.example.WebProject.softUni.dtos.*;
import WebProject.example.WebProject.softUni.model.Movie;
import WebProject.example.WebProject.softUni.model.Review;
import WebProject.example.WebProject.softUni.model.User;
import WebProject.example.WebProject.softUni.repositories.ReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserHelperService userHelperService;
    private final ModelMapper modelMapper;

    public ReviewService(ReviewRepository reviewRepository, UserHelperService userHelperService, ModelMapper modelMapper) {
        this.reviewRepository = reviewRepository;
        this.userHelperService = userHelperService;
        this.modelMapper = modelMapper;
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
        review.setLikes(0);
        review.setMovie(mappedMovie);
        review.setUser(this.userHelperService.getUser());
        return review;
    }

    public void deleteReview(Review review) {
        this.reviewRepository.delete(review);
    }

    public void updateReview(EditReviewDto newReviewData, Review review) {
        updateFieldIfNotBlank(newReviewData::getReviewTitle, review::setReviewTitle);
        updateFieldIfValidRating(newReviewData.getReviewRating(), review::setRating);
        updateFieldIfNotBlank(newReviewData::getReviewText, review::setReviewText);

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

    public void likeReview(Long reviewId) {
        Optional<Review> byId = this.reviewRepository.findById(reviewId);
        if (byId.isPresent()) {
            Review review = byId.get();
            review.setLikes(review.getLikes() + 1);
            this.reviewRepository.save(review);
        }
    }

    public void dislikeReview(Long reviewId) {
        Optional<Review> byId = this.reviewRepository.findById(reviewId);
        if (byId.isPresent()) {
            Review review = byId.get();
            int likes = review.getLikes();
            if (likes - 1 >= 0) {
                review.setLikes(review.getLikes() - 1);
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
