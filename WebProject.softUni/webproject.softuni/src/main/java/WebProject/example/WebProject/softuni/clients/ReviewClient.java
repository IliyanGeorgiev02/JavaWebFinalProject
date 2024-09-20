package webproject.example.webproject.softuni.clients;

import jakarta.transaction.Transactional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import webproject.example.webproject.softuni.dtos.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewClient {
    private final RestClient restClient;
    private final String reviewServiceUrl = "http://localhost:8081";

    public ReviewClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<ReviewFullInfoDto> getReviewsByUserId(long id) {
        String url = reviewServiceUrl + "/reviews/" + id;
        return restClient.get().uri(url).accept(MediaType.APPLICATION_JSON).retrieve().body(new ParameterizedTypeReference<List<ReviewFullInfoDto>>() {
        });
    }

    @Transactional
    public ReviewFullInfoDto createReview(ReviewFullInfoDto reviewFullInfoDto) {
        String url = reviewServiceUrl + "/review/save";
        return restClient.post().uri(url).accept(MediaType.APPLICATION_JSON).body(reviewFullInfoDto).retrieve().body(ReviewFullInfoDto.class);
    }

    public Optional<ReviewFullInfoDto> getReviewById(Long reviewId) {
        String url = reviewServiceUrl + "/review/" + reviewId;
        return restClient.get().uri(url).accept(MediaType.APPLICATION_JSON).retrieve().body(new ParameterizedTypeReference<Optional<ReviewFullInfoDto>>() {
        });
    }

    public void deleteReview(Long id) {
        String url = reviewServiceUrl + "/review/delete/" + id;

        try {
            HttpStatusCode statusCode = restClient.delete()
                    .uri(url)
                    .retrieve()
                    .toBodilessEntity()
                    .getStatusCode();

            if (!statusCode.is2xxSuccessful()) {
                throw new RuntimeException("Failed to delete review. Status Code: " + statusCode);
            }
        } catch (RestClientResponseException e) {
            throw new RuntimeException("Error response from server: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Exception during review deletion: " + e.getMessage(), e);
        }
    }

    public void updateReview(EditReviewDto newReviewData, ReviewFullInfoDto review) {
        String url = reviewServiceUrl + "/review/update";

        // Create the request payload
        UpdateReviewRequest updateRequest = new UpdateReviewRequest(newReviewData, review);

        try {
            HttpStatusCode statusCode = restClient.put()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(updateRequest)
                    .retrieve()
                    .toBodilessEntity()
                    .getStatusCode();

            if (!statusCode.is2xxSuccessful()) {
                throw new RuntimeException("Failed to update review. Status Code: " + statusCode);
            }
        } catch (RestClientResponseException e) {
            throw new RuntimeException("Error response from server: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Exception during review update: " + e.getMessage(), e);
        }
    }

    public List<ReviewFullInfoDto> getALLReviews() {
        String url = reviewServiceUrl + "/review/all";
        List<ReviewFullInfoDto> reviews = this.restClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ReviewFullInfoDto>>() {
                });
        return reviews != null ? reviews : new ArrayList<>();
    }


    public void likeReview(Long reviewId, Long userId) {
        String url = reviewServiceUrl + "/review/like/" + reviewId + "/" + userId;
        this.restClient.post().uri(url).accept(MediaType.APPLICATION_JSON).body(userId).retrieve().toBodilessEntity();
    }

    public void dislikeReview(Long reviewId, Long userId) {
        String url = reviewServiceUrl + "/review/dislike/" + reviewId + "/" + userId;
        this.restClient.post().uri(url).accept(MediaType.APPLICATION_JSON).body(userId).retrieve().toBodilessEntity();
    }

    public List<ReviewFullInfoDto> findALLReviewsByMovieId(long id) {
        String url = reviewServiceUrl + "/reviews/movie/" + id;
        return this.restClient.get().uri(url).accept(MediaType.APPLICATION_JSON).retrieve().body(new ParameterizedTypeReference<List<ReviewFullInfoDto>>() {
        });
    }

    public DisplayReviewDto mapReviewsToDisplayReviewDto(List<ReviewFullInfoDto> reviews) {
        List<MovieReviewInfoDto> movieReviewInfoDtos = reviews.stream()
                .map(this::convertToMovieReviewInfoDto)
                .collect(Collectors.toList());
        DisplayReviewDto displayReviewDto = new DisplayReviewDto();
        displayReviewDto.setReviews(movieReviewInfoDtos);
        return displayReviewDto;
    }

    private MovieReviewInfoDto convertToMovieReviewInfoDto(ReviewFullInfoDto review) {
        MovieReviewInfoDto movieReviewInfoDto = new MovieReviewInfoDto();
        movieReviewInfoDto.setMovieTitle(review.getMovieTitle());
        movieReviewInfoDto.setDirector(review.getDirector());
        movieReviewInfoDto.setPosterUrl(review.getPosterUrl());
        movieReviewInfoDto.setReviewTitle(review.getReviewTitle());
        movieReviewInfoDto.setReviewRating(String.valueOf(review.getRating()));
        return movieReviewInfoDto;
    }

    public ReviewListDto mapReviewsToDto(List<ReviewFullInfoDto> reviews) {
        ReviewListDto reviewListDto = new ReviewListDto();
        reviewListDto.setReviews(reviews);
        return reviewListDto;
    }


}
