package WebProject.example.WebProject.softUni.services;

import WebProject.example.WebProject.softUni.controllers.MovieController;
import WebProject.example.WebProject.softUni.dtos.MovieFullInfoDto;
import WebProject.example.WebProject.softUni.dtos.MovieResponseDto;
import WebProject.example.WebProject.softUni.dtos.MovieSearchDto;
import WebProject.example.WebProject.softUni.dtos.OMDBSearchResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class OmdbService {
    private final String apiKey = "8c6cfa03";
    private final String baseUrl = "http://www.omdbapi.com/?apikey=" + apiKey;
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    public OMDBSearchResponseDto searchByTitle(String title) {
        RestTemplate restTemplate = new RestTemplate();
        String url = baseUrl + "&s=" + title;
        try {
            return restTemplate.getForObject(url, OMDBSearchResponseDto.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("OMDB API error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while calling the OMDB API: " + e.getMessage());
        }
    }

    public MovieFullInfoDto searchById(String imdbId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = baseUrl + "&i=" + imdbId;
        logger.info(url);
        try {
            MovieFullInfoDto fullInfoDto = restTemplate.getForObject(url, MovieFullInfoDto.class);
            logger.info(fullInfoDto.toString());
            return fullInfoDto;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("OMDB API error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while calling the OMDB API: " + e.getMessage());
        }
    }

    public MovieResponseDto searchByTitleAndYear(String title, String year) {
        RestTemplate restTemplate = new RestTemplate();
        String url = baseUrl + "&t=" + title + "&y=" + year;
        try {
            return restTemplate.getForObject(url, MovieResponseDto.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("OMDB API error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while calling the OMDB API: " + e.getMessage());
        }
    }
}
