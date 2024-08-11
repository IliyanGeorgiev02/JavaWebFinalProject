package WebProject.example.WebProject.softUni.controllers;

import WebProject.example.WebProject.softUni.dtos.MovieFullInfoDto;
import WebProject.example.WebProject.softUni.dtos.ReviewListDto;
import WebProject.example.WebProject.softUni.model.Movie;
import WebProject.example.WebProject.softUni.model.Review;
import WebProject.example.WebProject.softUni.services.MovieService;
import WebProject.example.WebProject.softUni.services.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ModelMapper modelMapper;

    private Movie testMovie;
    private Review testReview;

    @BeforeEach
    void setUp() {
        testMovie = new Movie();
        testMovie.setId(1L);
        testMovie.setTitle("Test Movie");
        testMovie.setDescription("A test movie description");

        testReview = new Review();
        testReview.setId(1L);
        testReview.setReviewTitle("Test Review");
        testReview.setReviewText("A test review text");
        testReview.setRating(5);
        testReview.setMovie(testMovie);

        when(movieService.findMovieById(1L)).thenReturn(Optional.of(testMovie));
        when(reviewService.findALLReviewsByMovieId(1L)).thenReturn(List.of(testReview));
        when(movieService.mapMovieShortInfo(testMovie)).thenReturn(new MovieFullInfoDto());
        when(reviewService.mapReviewsToDto(List.of(testReview))).thenReturn(new ReviewListDto());
    }

    @Test
    void testGetMovie() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/Movie/{id}", 1)
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("Movie"))
                .andExpect(model().attributeExists("movieData"))
                .andExpect(model().attributeExists("reviewsData"));
    }

    @Test
    void testGetMovieNotFound() throws Exception {
        when(movieService.findMovieById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/Movie/{id}", 1)
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("Home"));
    }

}
