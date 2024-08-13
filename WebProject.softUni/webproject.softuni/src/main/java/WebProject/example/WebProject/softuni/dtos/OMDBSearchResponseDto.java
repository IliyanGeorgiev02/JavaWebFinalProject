package webproject.example.webproject.softuni.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class OMDBSearchResponseDto implements Serializable {
    @JsonProperty("Search")
    private List<MovieResponseDto> search;

    @JsonProperty("totalResults")
    private String totalResults;

    @JsonProperty("Response")
    private String response;


    public List<MovieResponseDto> getSearch() {
        return search;
    }

    public void setSearch(List<MovieResponseDto> search) {
        this.search = search;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
