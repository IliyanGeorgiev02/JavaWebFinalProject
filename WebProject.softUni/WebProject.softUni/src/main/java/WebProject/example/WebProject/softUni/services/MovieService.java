package WebProject.example.WebProject.softUni.services;

import WebProject.example.WebProject.softUni.dtos.MovieFullInfoDto;
import WebProject.example.WebProject.softUni.model.Movie;
import WebProject.example.WebProject.softUni.repositories.MovieRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final ModelMapper modelMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    public MovieService(MovieRepository movieRepository, ModelMapper modelMapper) {
        this.movieRepository = movieRepository;
        this.modelMapper = modelMapper;
    }

    public void saveMovie(Movie mappedMovie) {
        this.movieRepository.saveAndFlush(mappedMovie);
    }

    public Optional<Movie> findMovie(Movie mappedMovie) {
        return this.movieRepository.findByImdbID(mappedMovie.getImdbId());
    }

    public Movie mapMovie(MovieFullInfoDto movieFullInfoDto) {
        Movie mappedMovie;
        mappedMovie = this.modelMapper.map(movieFullInfoDto, Movie.class);
        mappedMovie.setReleased(LocalDate.parse(movieFullInfoDto.getReleaseDate(),formatter));
        mappedMovie.setYear(Year.parse(movieFullInfoDto.getYear()));
        mappedMovie.setWriters(movieFullInfoDto.getWriter());
        mappedMovie.setAudienceRating(movieFullInfoDto.getRatings().toString());
        return mappedMovie;
    }

    public List<Movie> findAllMovies() {
        return this.movieRepository.findAll();
    }

    public Optional<Movie> findMovieById(long id) {
        return this.movieRepository.findById(id);
    }
}
