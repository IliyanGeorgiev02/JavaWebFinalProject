package webproject.example.webproject.softuni.repositories;

import webproject.example.webproject.softuni.model.CustomList;
import webproject.example.webproject.softuni.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomListRepository extends JpaRepository<CustomList, Long> {
    @Query("SELECT cl.movies FROM CustomList cl WHERE cl.title = :title AND cl.description = :description")
    List<Movie> findMoviesByTitleAndDescription(@Param("title") String title, @Param("description") String description);

    @Query("SELECT c FROM CustomList c WHERE c.title = :title AND c.description = :description")
    CustomList findByTitleAndDescription(@Param("title") String title, @Param("description") String description);

    @Query("SELECT c FROM CustomList c WHERE c.user.username = :username")
    List<CustomList> findByUsername(@Param("username") String username);

    @Query("SELECT c FROM CustomList c WHERE c.user.id = :userId")
    List<CustomList> findByUserId(@Param("userId") long id);

}
