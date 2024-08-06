package WebProject.example.WebProject.softUni.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movies")
public class Movie extends BaseEntity {
    private String title;
    @Column(name = "release_year")
    private Year year;
    @Column(name = "audience_rating")
    private String audienceRating;
    @Column(name = "release_date")
    private LocalDate released;
    private String runtime;
    private String genre;
    private String director;
    private String writers;
    private String actors;
    private String description;
    private String languages;
    private String country;
    private String awards;
    @Column(name = "poster_URL")
    private String posterUrl;
    @Column(name = "imdb_id", unique = true)
    private String imdbId;
    private String scores;
    private String type;
    @Column(name = "box_office")
    private String boxOffice;
    private String production;
    private String website;
    @ManyToMany(mappedBy = "movies")
    private List<CustomList> customLists;
    @OneToMany(mappedBy = "movie")
    private List<Review> reviews;

    public Movie() {
        this.customLists = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    public LocalDate getReleased() {
        return released;
    }

    public void setReleased(LocalDate released) {
        this.released = released;
    }

    public String getLanguages() {
        return languages;
    }

    public String getScores() {
        return scores;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public String getAudienceRating() {
        return audienceRating;
    }

    public void setAudienceRating(String audienceRating) {
        this.audienceRating = audienceRating;
    }


    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getWriters() {
        return writers;
    }

    public void setWriters(String writers) {
        this.writers = writers;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public void setScores(String scores) {
        this.scores = scores;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBoxOffice() {
        return boxOffice;
    }

    public void setBoxOffice(String boxOffice) {
        this.boxOffice = boxOffice;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<CustomList> getCustomLists() {
        return customLists;
    }

    public void setCustomLists(List<CustomList> customLists) {
        this.customLists = customLists;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
