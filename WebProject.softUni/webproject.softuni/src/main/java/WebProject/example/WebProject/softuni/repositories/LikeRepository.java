package webproject.example.webproject.softuni.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webproject.example.webproject.softuni.model.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like,Long> {
}
