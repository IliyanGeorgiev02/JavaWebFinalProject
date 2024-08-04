package WebProject.example.WebProject.softUni.repositories;

import WebProject.example.WebProject.softUni.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("SELECT m FROM Movie m WHERE m.imdbId = :imdbId")
   Optional<Movie> findByImdbID(@Param("imdbId") String imdbId);
}
