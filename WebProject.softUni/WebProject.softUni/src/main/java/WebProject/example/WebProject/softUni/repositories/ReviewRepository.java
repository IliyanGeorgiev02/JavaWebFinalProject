package WebProject.example.WebProject.softUni.repositories;

import WebProject.example.WebProject.softUni.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
