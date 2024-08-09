package WebProject.example.WebProject.softUni.dtos;

public class DisplayMovieInListDto {
    private String title;
    private String year;
    private String posterUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public DisplayMovieInListDto() {
    }

    public DisplayMovieInListDto(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
}
