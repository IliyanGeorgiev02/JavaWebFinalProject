package WebProject.example.WebProject.softUni.services;

import WebProject.example.WebProject.softUni.dtos.*;
import WebProject.example.WebProject.softUni.model.Movie;
import WebProject.example.WebProject.softUni.model.Review;
import WebProject.example.WebProject.softUni.model.User;
import WebProject.example.WebProject.softUni.repositories.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserHelperService userHelperService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private User user;

    @Mock
    private Movie movie;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Mock
    private AddReviewDto addReviewDto;

    @Test
    void updateReview_whenAllFieldsValid_thenUpdatesReview() {
        EditReviewDto newReviewData = new EditReviewDto();
        newReviewData.setReviewTitle("New Title");
        newReviewData.setReviewRating(5);
        newReviewData.setReviewText("Updated review text.");

        Review review = new Review();
        review.setReviewTitle("Old Title");
        review.setRating(3);
        review.setReviewText("Old review text.");

        when(reviewRepository.save(review)).thenReturn(review);

        reviewService.updateReview(newReviewData, review);

        assertEquals("New Title", review.getReviewTitle());
        assertEquals(5, review.getRating());
        assertEquals("Updated review text.", review.getReviewText());
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    void updateReview_whenTitleIsBlank_thenDoesNotUpdateTitle() {
        EditReviewDto newReviewData = new EditReviewDto();
        newReviewData.setReviewTitle("");
        newReviewData.setReviewRating(4);
        newReviewData.setReviewText("Updated review text.");

        Review review = new Review();
        review.setReviewTitle("Old Title");
        review.setRating(3);
        review.setReviewText("Old review text.");

        when(reviewRepository.save(review)).thenReturn(review);

        reviewService.updateReview(newReviewData, review);

        assertEquals("Old Title", review.getReviewTitle());
        assertEquals(4, review.getRating());
        assertEquals("Updated review text.", review.getReviewText());
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    void updateReview_whenRatingIsInvalid_thenDoesNotUpdateRating() {
        EditReviewDto newReviewData = new EditReviewDto();
        newReviewData.setReviewTitle("New Title");
        newReviewData.setReviewRating(0);
        newReviewData.setReviewText("Updated review text.");

        Review review = new Review();
        review.setReviewTitle("Old Title");
        review.setRating(3);
        review.setReviewText("Old review text.");

        when(reviewRepository.save(review)).thenReturn(review);

        reviewService.updateReview(newReviewData, review);

        assertEquals("New Title", review.getReviewTitle());
        assertEquals(3, review.getRating());
        assertEquals("Updated review text.", review.getReviewText());
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    void likeReview_whenReviewExists_thenIncrementsLikes() {
        Long reviewId = 1L;
        Review review = new Review();
        review.setLikes(5);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);

        reviewService.likeReview(reviewId);

        assertEquals(6, review.getLikes());
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    void likeReview_whenReviewDoesNotExist_thenNoAction() {
        Long reviewId = 1L;

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        reviewService.likeReview(reviewId);

        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void dislikeReview_whenReviewExistsAndLikesGreaterThanZero_thenDecrementsLikes() {
        Long reviewId = 1L;
        Review review = new Review();
        review.setLikes(5);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);

        reviewService.dislikeReview(reviewId);

        assertEquals(4, review.getLikes());
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    void dislikeReview_whenReviewExistsAndLikesZero_thenNoNegativeLikes() {
        Long reviewId = 1L;
        Review review = new Review();
        review.setLikes(0);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.save(review)).thenReturn(review);

        reviewService.dislikeReview(reviewId);

        assertEquals(0, review.getLikes());
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, never()).save(review);
    }

    @Test
    void dislikeReview_whenReviewDoesNotExist_thenNoAction() {
        Long reviewId = 1L;

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        reviewService.dislikeReview(reviewId);

        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void findAllReviews_returnsListOfReviews() {
        Review review1 = new Review();
        Review review2 = new Review();
        List<Review> reviews = List.of(review1, review2);

        when(reviewRepository.findAll()).thenReturn(reviews);

        List<Review> result = reviewService.findALLReviews();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(review1));
        assertTrue(result.contains(review2));
    }

    @Test
    void saveReview_savesReview() {
        Review review = new Review();

        reviewService.saveReview(review);

        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    void findReviewById_whenReviewExists_returnsReview() {
        Long reviewId = 1L;
        Review review = new Review();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        Optional<Review> result = reviewService.findReviewById(reviewId);

        assertTrue(result.isPresent());
        assertEquals(review, result.get());
    }

    @Test
    void findReviewById_whenReviewDoesNotExist_returnsEmpty() {
        Long reviewId = 1L;

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        Optional<Review> result = reviewService.findReviewById(reviewId);

        assertFalse(result.isPresent());
    }

    @Test
    void mapReview_mapsDtoToReview() {
        when(addReviewDto.getReviewTitle()).thenReturn("Great Movie");
        when(addReviewDto.getReviewText()).thenReturn("I really enjoyed it.");
        when(addReviewDto.getReviewRating()).thenReturn(5);
        when(userHelperService.getUser()).thenReturn(user);

        Review result = reviewService.mapReview(addReviewDto, movie);

        assertNotNull(result);
        assertEquals("Great Movie", result.getReviewTitle());
        assertEquals("I really enjoyed it.", result.getReviewText());
        assertEquals(5, result.getRating());
        assertEquals(0, result.getLikes());
        assertEquals(movie, result.getMovie());
        assertEquals(user, result.getUser());
    }

    @Test
    void deleteReview_deletesReview() {
        Review review = new Review();

        reviewService.deleteReview(review);

        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    void findAllReviewsByUser_returnsListOfReviews() {
        long userId = 1L;
        Review review1 = new Review();
        Review review2 = new Review();
        List<Review> reviews = List.of(review1, review2);

        when(reviewRepository.findByUserId(userId)).thenReturn(reviews);

        List<Review> result = reviewService.findALLReviewsByUser(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(review1));
        assertTrue(result.contains(review2));
    }

    @Test
    void mapReviewsToDisplayReviewDto_mapsReviewsToDto() {
        Review review1 = new Review();
        review1.setReviewTitle("Great movie");
        review1.setRating(5);
        Movie movie1 = new Movie();
        movie1.setTitle("Inception");
        movie1.setDirector("Christopher Nolan");
        movie1.setPosterUrl("url1");
        review1.setMovie(movie1);

        Review review2 = new Review();
        review2.setReviewTitle("Good movie");
        review2.setRating(4);
        Movie movie2 = new Movie();
        movie2.setTitle("Interstellar");
        movie2.setDirector("Christopher Nolan");
        movie2.setPosterUrl("url2");
        review2.setMovie(movie2);

        List<Review> reviews = List.of(review1, review2);

        DisplayReviewDto expectedDto = new DisplayReviewDto();
        expectedDto.setReviews(reviews.stream()
                .map(r -> {
                    MovieReviewInfoDto dto = new MovieReviewInfoDto();
                    dto.setMovieTitle(r.getMovie().getTitle());
                    dto.setDirector(r.getMovie().getDirector());
                    dto.setPosterUrl(r.getMovie().getPosterUrl());
                    dto.setReviewTitle(r.getReviewTitle());
                    dto.setReviewRating(String.valueOf(r.getRating()));
                    return dto;
                })
                .collect(Collectors.toList()));

        DisplayReviewDto result = reviewService.mapReviewsToDisplayReviewDto(reviews);

        assertNotNull(result);
        assertEquals(expectedDto.getReviews().size(), result.getReviews().size());
        assertEquals(expectedDto.getReviews().get(0).getMovieTitle(), result.getReviews().get(0).getMovieTitle());
        assertEquals(expectedDto.getReviews().get(1).getMovieTitle(), result.getReviews().get(1).getMovieTitle());
    }

    @Test
    void mapReviewData_mapsReviewToDto() {
        long reviewId = 1L;
        Review review = new Review();
        review.setId(reviewId);
        review.setReviewTitle("Amazing movie");
        review.setReviewText("Really enjoyed it.");
        review.setRating(5);
        review.setLikes(10);

        Movie movie = new Movie();
        movie.setTitle("The Matrix");
        movie.setYear(Year.parse("2000"));
        movie.setPosterUrl("matrixPosterUrl");

        User user = new User();
        user.setId(2L);
        user.setUsername("user123");

        review.setMovie(movie);
        review.setUser(user);

        ReviewFullInfoDto expectedDto = new ReviewFullInfoDto();
        expectedDto.setId(reviewId);
        expectedDto.setMovieTitle(movie.getTitle());
        expectedDto.setMovieRelease(String.valueOf(movie.getYear()));
        expectedDto.setReviewTitle(review.getReviewTitle());
        expectedDto.setReviewText(review.getReviewText());
        expectedDto.setRating(review.getRating());
        expectedDto.setLikes(review.getLikes());
        expectedDto.setUsername(user.getUsername());
        expectedDto.setPosterUrl(movie.getPosterUrl());
        expectedDto.setUserId(user.getId());

        when(modelMapper.map(review, ReviewFullInfoDto.class)).thenReturn(expectedDto);

        ReviewFullInfoDto result = reviewService.mapReviewData(review);

        assertNotNull(result);
        assertEquals(expectedDto, result);
    }

    @Test
    void mapReviewsToDto_mapsReviewsToReviewListDto() {
        Review review1 = new Review();
        review1.setId(1L);
        review1.setReviewTitle("Excellent");
        review1.setReviewText("Loved it!");
        review1.setRating(5);
        review1.setLikes(20);

        Review review2 = new Review();
        review2.setId(2L);
        review2.setReviewTitle("Okay");
        review2.setReviewText("It was alright.");
        review2.setRating(3);
        review2.setLikes(5);

        List<Review> reviews = List.of(review1, review2);

        ReviewFullInfoDto dto1 = new ReviewFullInfoDto();
        dto1.setId(review1.getId());
        dto1.setReviewTitle(review1.getReviewTitle());
        dto1.setReviewText(review1.getReviewText());
        dto1.setRating(review1.getRating());
        dto1.setLikes(review1.getLikes());

        ReviewFullInfoDto dto2 = new ReviewFullInfoDto();
        dto2.setId(review2.getId());
        dto2.setReviewTitle(review2.getReviewTitle());
        dto2.setReviewText(review2.getReviewText());
        dto2.setRating(review2.getRating());
        dto2.setLikes(review2.getLikes());

        ReviewListDto expectedDto = new ReviewListDto();
        expectedDto.setReviews(List.of(dto1, dto2));

        when(modelMapper.map(review1, ReviewFullInfoDto.class)).thenReturn(dto1);
        when(modelMapper.map(review2, ReviewFullInfoDto.class)).thenReturn(dto2);

        ReviewListDto result = reviewService.mapReviewsToDto(reviews);

        assertNotNull(result);
        assertEquals(expectedDto.getReviews().size(), result.getReviews().size());
        assertEquals(dto1, result.getReviews().get(0));
        assertEquals(dto2, result.getReviews().get(1));
    }

    @Test
    void findAllReviewsByMovieId_returnsListOfReviews() {
        long movieId = 1L;
        Review review1 = new Review();
        Review review2 = new Review();
        List<Review> reviews = List.of(review1, review2);

        when(reviewRepository.findByMovieId(movieId)).thenReturn(reviews);

        List<Review> result = reviewService.findALLReviewsByMovieId(movieId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(review1));
        assertTrue(result.contains(review2));
    }

}
