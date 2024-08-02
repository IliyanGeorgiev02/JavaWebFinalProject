package WebProject.example.WebProject.softUni.services;

import WebProject.example.WebProject.softUni.model.Review;
import WebProject.example.WebProject.softUni.repositories.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> findALLReviews() {
        return this.reviewRepository.findAll();
    }
}
