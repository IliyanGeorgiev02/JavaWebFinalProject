package WebProject.example.WebProject.softUni.dtos;

import jakarta.validation.constraints.NotNull;

public class AddMovieToListDto {
    @NotNull
    private String movieTitle;
    @NotNull
    private String movieYear;
    @NotNull
    private long listId;

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieYear() {
        return movieYear;
    }

    public void setMovieYear(String movieYear) {
        this.movieYear = movieYear;
    }

    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }
}
