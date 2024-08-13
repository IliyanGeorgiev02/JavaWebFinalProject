package webproject.example.webproject.softuni.repositories;

import webproject.example.webproject.softuni.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Year;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("SELECT m FROM Movie m WHERE m.imdbId = :imdbId")
    Optional<Movie> findByImdbID(@Param("imdbId") String imdbId);

    @Query("SELECT m FROM Movie m WHERE m.title = :title and m.year=:year")
    Optional<Movie> findByTitleAndYear(@Param("title") String title, @Param("year") Year year);
}
