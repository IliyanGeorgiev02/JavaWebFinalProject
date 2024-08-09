package WebProject.example.WebProject.softUni.services;

import WebProject.example.WebProject.softUni.dtos.ListOfMoviesDto;
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
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final ModelMapper modelMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    public MovieService(MovieRepository movieRepository, ModelMapper modelMapper) {
        this.movieRepository = movieRepository;
        this.modelMapper = modelMapper;
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
        mappedMovie.setReleased(LocalDate.parse(movieFullInfoDto.getReleaseDate(),formatter));
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

    private MovieFullInfoDto convertToDto(Movie movie) {
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
}
