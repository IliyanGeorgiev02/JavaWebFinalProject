package webproject.example.webproject.softuni.services;

import jakarta.transaction.Transactional;
import webproject.example.webproject.softuni.dtos.ListOfMoviesDto;
import webproject.example.webproject.softuni.dtos.MovieFullInfoDto;
import webproject.example.webproject.softuni.dtos.ReviewFullInfoDto;
import webproject.example.webproject.softuni.model.Movie;
import webproject.example.webproject.softuni.model.User;
import webproject.example.webproject.softuni.repositories.MovieRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final ModelMapper modelMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
    private final Map<String, Movie> movieCache = new ConcurrentHashMap<>();
    private final UserService userService;

    public MovieService(MovieRepository movieRepository, ModelMapper modelMapper, UserService userService) {
        this.movieRepository = movieRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    public Movie saveMovie(Movie mappedMovie) {
        return this.movieRepository.saveAndFlush(mappedMovie);
    }

    public Optional<Movie> findMovie(Movie mappedMovie) {
        return this.movieRepository.findByImdbID(mappedMovie.getImdbId());
    }

    public Movie mapMovie(MovieFullInfoDto movieFullInfoDto) {
        Movie mappedMovie;
        mappedMovie = this.modelMapper.map(movieFullInfoDto, Movie.class);
        mappedMovie.setReleased(LocalDate.parse(movieFullInfoDto.getReleaseDate(), formatter));
        mappedMovie.setYear(Year.parse(movieFullInfoDto.getYear()));
        mappedMovie.setWriters(movieFullInfoDto.getWriter());
        mappedMovie.setAudienceRating(movieFullInfoDto.getRatings().toString());
        mappedMovie.setDescription(movieFullInfoDto.getPlot());
        mappedMovie.setLanguages(movieFullInfoDto.getLanguage());
        return mappedMovie;
    }

    public List<Movie> findAllMovies() {
        return this.movieRepository.findAll();
    }

    public Optional<Movie> findMovieById(long id) {
        return this.movieRepository.findById(id);
    }

    public MovieFullInfoDto mapMovieShortInfo(Movie movie) {
        MovieFullInfoDto mappedMovie = this.modelMapper.map(movie, MovieFullInfoDto.class);
        mappedMovie.setPlot(movie.getDescription());
        mappedMovie.setReleaseDate(movie.getReleased().toString());
        mappedMovie.setId(movie.getId());
        return mappedMovie;
    }

    public ListOfMoviesDto mapMoviesToDto(List<Movie> movies) {
        List<MovieFullInfoDto> movieDtos = movies.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        ListOfMoviesDto listOfMoviesDto = new ListOfMoviesDto();
        listOfMoviesDto.setMovies(movieDtos);
        return listOfMoviesDto;
    }

    public MovieFullInfoDto convertToDto(Movie movie) {
        MovieFullInfoDto dto = new MovieFullInfoDto();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setYear(movie.getYear().toString());
        dto.setReleaseDate(movie.getReleased().toString());
        dto.setRuntime(movie.getRuntime());
        dto.setGenre(movie.getGenre());
        dto.setDirector(movie.getDirector());
        dto.setActors(movie.getActors());
        dto.setPlot(movie.getDescription());
        dto.setPosterUrl(movie.getPosterUrl());
        dto.setImdbId(movie.getImdbId());
        return dto;
    }

    public Optional<Movie> findMovieByTitleAndYear(String title, Year year) {
        return this.movieRepository.findByTitleAndYear(title, year);
    }

    public void cacheSelectedMovie(Movie movie) {
        movieCache.put("current", movie);
    }

    public Movie getSelectedMovie() {
        return movieCache.get("current");
    }

    public String findByReviewId(Long reviewId) {
        Movie byReviewId = movieRepository.findByReviewId(reviewId);
        return byReviewId.getTitle() + "," + byReviewId.getReleased() + "," + byReviewId.getPosterUrl()+","+byReviewId.getId();
    }

    @Transactional
    public void handleLocalUpdates(Movie movie, ReviewFullInfoDto createdReview, User user, Movie mappedMovie) {
        movie.getReviewsIds().add(createdReview.getId());
        user.getReview_ids().add(createdReview.getId());
        this.movieRepository.saveAndFlush(movie);
        this.userService.saveUser(user);
        this.movieRepository.saveAndFlush(mappedMovie);
    }
}
