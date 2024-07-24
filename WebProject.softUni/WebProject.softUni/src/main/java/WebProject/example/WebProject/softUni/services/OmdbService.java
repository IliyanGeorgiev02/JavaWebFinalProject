package WebProject.example.WebProject.softUni.services;

import WebProject.example.WebProject.softUni.dtos.OMDBSearchResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class OmdbService {
    private final String apiKey = "8c6cfa03";
    private final String baseUrl = "http://www.omdbapi.com/?apikey=" + apiKey + "&i=";

    public OMDBSearchResponseDto searchByTitle(String title) {
        RestTemplate restTemplate = new RestTemplate();
        String url = baseUrl + title;
        try {
            return restTemplate.getForObject(url, OMDBSearchResponseDto.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("OMDB API error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while calling the OMDB API: " + e.getMessage());
        }
    }
    public OMDBSearchResponseDto searchById(String imdbId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = baseUrl + imdbId;
        try {
            return restTemplate.getForObject(url, OMDBSearchResponseDto.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("OMDB API error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while calling the OMDB API: " + e.getMessage());
        }
    }
}
