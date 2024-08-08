package WebProject.example.WebProject.softUni.services;

import WebProject.example.WebProject.softUni.dtos.AddReviewDto;
import WebProject.example.WebProject.softUni.dtos.EditReviewDto;
import WebProject.example.WebProject.softUni.model.Movie;
import WebProject.example.WebProject.softUni.model.Review;
import WebProject.example.WebProject.softUni.repositories.ReviewRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserHelperService userHelperService;

    public ReviewService(ReviewRepository reviewRepository, UserHelperService userHelperService) {
        this.reviewRepository = reviewRepository;
        this.userHelperService = userHelperService;
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
}
