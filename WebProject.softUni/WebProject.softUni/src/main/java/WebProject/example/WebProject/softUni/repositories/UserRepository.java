package WebProject.example.WebProject.softUni.repositories;

import WebProject.example.WebProject.softUni.model.CustomList;
import WebProject.example.WebProject.softUni.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    @Transactional
    @Query("SELECT cl FROM CustomList cl WHERE cl.user.id = :userId")
    List<CustomList> findAllByUserId(Long userId);
}
