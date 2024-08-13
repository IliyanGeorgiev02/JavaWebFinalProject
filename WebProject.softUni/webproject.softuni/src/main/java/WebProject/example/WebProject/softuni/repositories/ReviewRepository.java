package webproject.example.webproject.softuni.repositories;

import webproject.example.webproject.softuni.model.Review;
import webproject.example.webproject.softuni.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.user.id = :userId")
    List<Review> findByUserId(@Param("userId") long id);

    @Query("SELECT r FROM Review r WHERE r.movie.id = :movieId")
    List<Review> findByMovieId(@Param("movieId") long id);

    @Query("SELECT r.user FROM Review r WHERE r.id = :reviewId")
    Optional<User> findUserByReviewId(Long reviewId);
}
