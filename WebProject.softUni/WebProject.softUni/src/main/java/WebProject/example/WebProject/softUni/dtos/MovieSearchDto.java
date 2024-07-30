package WebProject.example.WebProject.softUni.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class MovieSearchDto implements Serializable {
    @JsonProperty("title")
    private String title;
    @JsonProperty("imdbID")
    private String imdbId;
    @JsonProperty("year")
    private String year;

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
