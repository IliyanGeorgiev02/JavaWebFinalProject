package WebProject.example.WebProject.softUni.dtos;

import WebProject.example.WebProject.softUni.model.CustomList;
import WebProject.example.WebProject.softUni.model.Review;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

public class MovieFullInfoDto {
    @JsonProperty("Search")
    private String title;
    @JsonProperty("Search")
    private Year releaseYear;
    @JsonProperty("Search")
    private String audienceRating;
    @JsonProperty("Search")
    private LocalDate releaseDate;
    @JsonProperty("Search")
    private String runtime;
    @JsonProperty("Search")
    private String genre;
    @JsonProperty("Search")
    private String director;
    @JsonProperty("Search")
    private List<String> writers;
    @JsonProperty("Search")
    private List<String> actors;
    private String description;
    private List<String> languages;
    private String country;
    private String awards;
    private String posterUrl;
    private List<String> scores;
    private String type;
    private String boxOffice;
    private String production;
    private String website;
}
