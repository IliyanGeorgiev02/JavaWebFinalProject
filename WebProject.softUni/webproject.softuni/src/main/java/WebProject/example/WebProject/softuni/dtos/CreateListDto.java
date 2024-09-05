package webproject.example.webproject.softuni.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateListDto {
    @NotBlank
    @Size(min = 3, max = 50)
    private String title;
    @NotBlank
    @Size(min = 3, max = 100)
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
