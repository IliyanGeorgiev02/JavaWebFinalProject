package WebProject.example.WebProject.softUni.services;

import WebProject.example.WebProject.softUni.dtos.ListOfMoviesDto;
import WebProject.example.WebProject.softUni.dtos.MovieFullInfoDto;
import WebProject.example.WebProject.softUni.model.Movie;
import WebProject.example.WebProject.softUni.repositories.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MovieServiceTest {
    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MovieService movieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveMovie_whenCalled_thenSavesAndFlushesMovie() {
        Movie movie = new Movie();
        when(movieRepository.saveAndFlush(movie)).thenReturn(movie);
        Movie result = movieService.saveMovie(movie);
        assertNotNull(result, "Result should not be null");
        verify(movieRepository, times(1)).saveAndFlush(movie);
    }

    @Test
    void findAllMovies_whenCalled_thenReturnsListOfMovies() {
        Movie movie1 = new Movie();
        Movie movie2 = new Movie();
        when(movieRepository.findAll()).thenReturn(List.of(movie1, movie2));
        List<Movie> result = movieService.findAllMovies();
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "List size should be 2");
        assertEquals(movie1, result.get(0), "First movie should match");
        assertEquals(movie2, result.get(1), "Second movie should match");
    }

    @Test
    void findMovieById_whenCalledWithId_thenReturnsOptionalOfMovie() {
        long id = 1L;
        Movie movie = new Movie();
        when(movieRepository.findById(id)).thenReturn(Optional.of(movie));
        Optional<Movie> result = movieService.findMovieById(id);
        assertTrue(result.isPresent(), "Result should be present");
        assertEquals(movie, result.get(), "Movie should match");
    }

    @Test
    void mapMovieShortInfo_whenCalledWithMovie_thenMapsToMovieFullInfoDto() {
        Movie movie = new Movie();
        movie.setDescription("A thrilling movie.");
        movie.setReleased(LocalDate.parse("2024-08-10"));
        movie.setId(1L);
        MovieFullInfoDto dto = new MovieFullInfoDto();
        when(modelMapper.map(movie, MovieFullInfoDto.class)).thenReturn(dto);
        dto.setPlot(movie.getDescription());
        dto.setReleaseDate(movie.getReleased().toString());
        dto.setId(movie.getId());
        MovieFullInfoDto result = movieService.mapMovieShortInfo(movie);
        assertNotNull(result, "Result should not be null");
        assertEquals(movie.getDescription(), result.getPlot(), "Plot should match");
        assertEquals(movie.getReleased().toString(), result.getReleaseDate(), "Release date should match");
        assertEquals(movie.getId(), result.getId(), "ID should match");
    }

    @Test
    void mapMoviesToDto_whenCalledWithListOfMovies_thenMapsToListOfMoviesDto() {
        Movie movie1 = new Movie();
        movie1.setId(1L);
        movie1.setTitle("Movie 1");
        movie1.setYear(Year.of(2024));
        movie1.setReleased(LocalDate.parse("2024-08-10"));
        movie1.setRuntime("120 min");
        movie1.setGenre("Action");
        movie1.setDirector("Director 1");
        movie1.setActors("Actor 1");
        movie1.setDescription("Description 1");
        movie1.setPosterUrl("posterUrl1");
        movie1.setImdbId("tt1234567");

        Movie movie2 = new Movie();
        movie2.setId(2L);
        movie2.setTitle("Movie 2");
        movie2.setYear(Year.of(2023));
        movie2.setReleased(LocalDate.parse("2023-05-15"));
        movie2.setRuntime("150 min");
        movie2.setGenre("Drama");
        movie2.setDirector("Director 2");
        movie2.setActors("Actor 2");
        movie2.setDescription("Description 2");
        movie2.setPosterUrl("posterUrl2");
        movie2.setImdbId("tt7654321");

        List<Movie> movies = Arrays.asList(movie1, movie2);
        ListOfMoviesDto result = movieService.mapMoviesToDto(movies);
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.getMovies().size(), "List size should be 2");

        MovieFullInfoDto dto1 = result.getMovies().get(0);
        assertEquals(movie1.getId(), dto1.getId(), "First movie ID should match");
        assertEquals(movie1.getTitle(), dto1.getTitle(), "First movie title should match");
        assertEquals(movie1.getYear().toString(), dto1.getYear(), "First movie year should match");
        assertEquals(movie1.getReleased().toString(), dto1.getReleaseDate(), "First movie release date should match");
        assertEquals(movie1.getRuntime(), dto1.getRuntime(), "First movie runtime should match");
        assertEquals(movie1.getGenre(), dto1.getGenre(), "First movie genre should match");
        assertEquals(movie1.getDirector(), dto1.getDirector(), "First movie director should match");
        assertEquals(movie1.getActors(), dto1.getActors(), "First movie actors should match");
        assertEquals(movie1.getDescription(), dto1.getPlot(), "First movie plot should match");
        assertEquals(movie1.getPosterUrl(), dto1.getPosterUrl(), "First movie poster URL should match");
        assertEquals(movie1.getImdbId(), dto1.getImdbId(), "First movie IMDb ID should match");

        MovieFullInfoDto dto2 = result.getMovies().get(1);
        assertEquals(movie2.getId(), dto2.getId(), "Second movie ID should match");
    }

    @Test
    void convertToDto_whenCalledWithMovie_thenMapsToMovieFullInfoDto() {
        // Arrange
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Sample Movie");
        movie.setYear(Year.of(2024));
        movie.setReleased(LocalDate.parse("2024-08-10"));
        movie.setRuntime("120 min");
        movie.setGenre("Adventure");
        movie.setDirector("Sample Director");
        movie.setActors("Sample Actor");
        movie.setDescription("Sample Description");
        movie.setPosterUrl("samplePosterUrl");
        movie.setImdbId("tt1234567");
        MovieFullInfoDto result = movieService.convertToDto(movie);
        assertNotNull(result, "Result should not be null");
        assertEquals(movie.getId(), result.getId(), "Movie ID should match");
        assertEquals(movie.getTitle(), result.getTitle(), "Movie title should match");
        assertEquals(movie.getYear().toString(), result.getYear(), "Movie year should match");
        assertEquals(movie.getReleased().toString(), result.getReleaseDate(), "Movie release date should match");
        assertEquals(movie.getRuntime(), result.getRuntime(), "Movie runtime should match");
        assertEquals(movie.getGenre(), result.getGenre(), "Movie genre should match");
        assertEquals(movie.getDirector(), result.getDirector(), "Movie director should match");
        assertEquals(movie.getActors(), result.getActors(), "Movie actors should match");
        assertEquals(movie.getDescription(), result.getPlot(), "Movie plot should match");
        assertEquals(movie.getPosterUrl(), result.getPosterUrl(), "Movie poster URL should match");
        assertEquals(movie.getImdbId(), result.getImdbId(), "Movie IMDb ID should match");
    }

    @Test
    void findMovieByTitleAndYear_whenCalledWithTitleAndYear_thenReturnsOptionalOfMovie() {
        String title = "Sample Movie";
        Year year = Year.of(2024);
        Movie movie = new Movie();
        when(movieRepository.findByTitleAndYear(title, year)).thenReturn(Optional.of(movie));
        Optional<Movie> result = movieService.findMovieByTitleAndYear(title, year);
        assertTrue(result.isPresent(), "Result should be present");
        assertEquals(movie, result.get(), "Movie should match");
    }
}
