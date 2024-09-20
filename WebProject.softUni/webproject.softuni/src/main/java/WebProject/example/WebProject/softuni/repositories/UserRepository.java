package webproject.example.webproject.softuni.repositories;

import webproject.example.webproject.softuni.model.CustomList;
import webproject.example.webproject.softuni.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    @Transactional
    @Query("SELECT cl FROM CustomList cl WHERE cl.user.id = :userId")
    List<CustomList> findAllByUserId(@Param("userId") Long userId);
}
